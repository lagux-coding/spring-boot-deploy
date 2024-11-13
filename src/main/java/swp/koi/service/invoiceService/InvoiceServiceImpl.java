package swp.koi.service.invoiceService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp.koi.dto.response.AuctionedFishPricesResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.*;
import swp.koi.model.enums.*;
import swp.koi.repository.AuctionRequestRepository;
import swp.koi.repository.InvoiceRepository;
import swp.koi.repository.LotRegisterRepository;
import swp.koi.repository.LotRepository;
import swp.koi.service.accountService.AccountService;
import swp.koi.service.authService.GetUserInfoByUsingAuth;
import swp.koi.service.memberService.MemberService;
import swp.koi.service.vnPayService.VnpayServiceImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService{

    private final InvoiceRepository invoiceRepository;
    private final VnpayServiceImpl vnpayService;
    private final GetUserInfoByUsingAuth getUserInfoByUsingAuth;
    private final LotRepository lotRepository;
    private final MemberService memberService;
    private final AccountService accountService;
    private final LotRegisterRepository lotRegisterRepository;
    private final AuctionRequestRepository auctionRequestRepository;


    @Override
    public Invoice createInvoiceForAuctionWinner(int lotId, int memberId) {
        Lot lot = lotRepository.findById(lotId).orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));
        Member member = memberService.getMemberById(memberId);
        LotRegister lotRegister = lotRegisterRepository.findByLotAndStatus(lot, LotRegisterStatusEnum.WON);

        return Invoice.builder()
                .invoiceDate(LocalDateTime.now())
                .tax((float) (lot.getCurrentPrice() * 0.1))
                .dueDate(LocalDateTime.now().plusWeeks(1))
                .subTotal(lot.getCurrentPrice())
                .paymentLink("")
                .lot(lot)
                .koiFish(lot.getKoiFish())
                .status(InvoiceStatusEnums.PENDING)
                .finalAmount((float) (lot.getCurrentPrice() * 1.1 - lot.getDeposit()))
                .priceWithoutShipFee((float) (lot.getCurrentPrice() * 1.1 - lot.getDeposit()))
                .member(member)
                .lotRegister(lotRegister)
                .build();
    }

    @Override
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void updateStatusOfInvoice() {
        List<Invoice> invoices = invoiceRepository.findAllByDueDateLessThanAndStatus(LocalDateTime.now(), InvoiceStatusEnums.PENDING );
        for (Invoice invoice : invoices) {
            invoice.setStatus(InvoiceStatusEnums.OVERDUE);
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public String regeneratePaymentLinkForInvoice(int invoiceId) throws UnsupportedEncodingException {

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new KoiException(ResponseCode.TRANSACTION_NOT_FOUND));
        String paymentLink = invoice.getPaymentLink();

        String queryParams = paymentLink.split("\\?")[1];
        Map<String, String> params = new HashMap<>();
        for (String param : queryParams.split("&")) {
            String[] keyValue = param.split("=");
            params.put(keyValue[0], keyValue[1]);
        }

        // Extract the vnp_OrderInfo value
        String vnpOrderInfo = params.get("vnp_OrderInfo");

        // Decode the vnp_OrderInfo value
        String decodedVnpOrderInfo = URLDecoder.decode(vnpOrderInfo, "UTF-8");

        // Extract the individual values
        String[] values = decodedVnpOrderInfo.split("&");
        int memberId = Integer.parseInt(values[0].split("=")[1]);
        int registerLot = Integer.parseInt(values[1].split("=")[1]);
        String type = values[2].split("=")[1];

        return vnpayService.generateInvoice(registerLot,memberId, TransactionTypeEnum.valueOf(type));
    }

    @Override
    public List<Invoice> getAllInvoicesForAuctionWinner() {

        Member member = getUserInfoByUsingAuth.getMemberFromAuth();

        return invoiceRepository.findAllByMember(member);
    }

    @Override
    public Invoice updateInvoiceAddress(float kilometer, int invoiceId, String address) throws UnsupportedEncodingException {
        Member member = getUserInfoByUsingAuth.getMemberFromAuth();
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));

        invoice.setAddress(address);

        invoice.setKilometers(kilometer);

        float currentPrice = invoice.getPriceWithoutShipFee();
        float newPriceWithAddress = generateShippingPriceForInvoice(kilometer);
        float newPrice = currentPrice + newPriceWithAddress;


        invoice.setFinalAmount(newPrice);
        invoiceRepository.save(invoice);

        String paymentLink = generatePaymentLink(invoice.getLot().getLotId(), member.getMemberId());
        invoice.setPaymentLink(paymentLink);

        return invoiceRepository.save(invoice);
    }

    private String generatePaymentLink(int lotId, int memberId) throws UnsupportedEncodingException {
        return vnpayService.generateInvoice(lotId, memberId, TransactionTypeEnum.INVOICE_PAYMENT);
    }

    private float generateShippingPriceForInvoice(float kilometer) {
        float pricePerKm = pricePerKilometer(kilometer);
        return (pricePerKm * kilometer);
    }

    private float pricePerKilometer(double kilometer) {
        int pricePerKm = 0;
        
        if (kilometer >= 11 && kilometer <= 50) {
            pricePerKm = 1500;
        } else if (kilometer >= 51 && kilometer <= 100) {
            pricePerKm = 1200;
        } else if (kilometer >= 101 && kilometer <= 200) {
            pricePerKm = 1000;
        } else if (kilometer > 200) {
            pricePerKm = 800;
        } else {
           return 0; // Invalid distance
        }
        
        return pricePerKm;
    }

    @Override
    public Invoice getInvoiceForSpecificLot(int lotId) {
        try {
            Lot lot = lotRepository.findById(lotId).orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));
            return invoiceRepository.findByLot(lot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void assignStaffDelivery(Integer invoiceId, Integer accountId) {
        Account account = accountService.findById(accountId);
        if(!account.getRole().equals(AccountRoleEnum.STAFF))
            throw new KoiException(ResponseCode.STAFF_NOT_FOUND);

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new KoiException(ResponseCode.INVOICE_NOT_FOUND));

        invoice.setStatus(InvoiceStatusEnums.DELIVERY_IN_PROGRESS);
        invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getAllDeliveringInvoices() {
        Account account = getUserInfoByUsingAuth.getAccountFromAuth();
        if(!account.getRole().equals(AccountRoleEnum.STAFF))
            throw  new KoiException(ResponseCode.STAFF_NOT_FOUND);

        return invoiceRepository.findAllByAccountAndStatus(account, InvoiceStatusEnums.DELIVERY_IN_PROGRESS);
    }

    @Override
    public List<Invoice> listOfInvoices(){
        List<InvoiceStatusEnums> statues = Arrays.asList(
                InvoiceStatusEnums.DELIVERY_IN_PROGRESS,
                InvoiceStatusEnums.DELIVERED,
                InvoiceStatusEnums.CANCELLED,
                InvoiceStatusEnums.FAILED
        );
        return invoiceRepository.findAllByStatusIn(statues);
    }

    @Override
    public void updateInvoiceStatus(Integer invoiceId, InvoiceStatusEnums status) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new KoiException(ResponseCode.INVOICE_NOT_FOUND));

        List<InvoiceStatusEnums> statues = Arrays.asList(
          InvoiceStatusEnums.DELIVERED,
          InvoiceStatusEnums.FAILED,
          InvoiceStatusEnums.CANCELLED
        );

        if(status.equals(InvoiceStatusEnums.DELIVERED)){
            AuctionRequest auctionRequest = invoice.getKoiFish().getAuctionRequest();
            auctionRequest.setAuctionFinalPrice(invoice.getSubTotal());
            auctionRequest.setStatus(AuctionRequestStatusEnum.WAITING_FOR_PAYMENT);
            auctionRequestRepository.save(auctionRequest);
        }

        if(statues.contains(status)){
            invoice.setStatus(status);
            invoiceRepository.save(invoice);
        }else
            throw new KoiException(ResponseCode.FAIL);
    }

    @Override
    public List<Invoice> listAllInvoicesForManager(){
        return invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public List<AuctionedFishPricesResponseDto> getAllAuctionedFishPrices() {
        List<Invoice> list = invoiceRepository.findAllByStatus(InvoiceStatusEnums.DELIVERED);
        List<AuctionedFishPricesResponseDto> listDto = list.stream().map(invoice -> {
            AuctionedFishPricesResponseDto dto = new AuctionedFishPricesResponseDto();
            dto.setSubTotal(invoice.getSubTotal());
            dto.setEndTime(invoice.getLot().getEndingTime());
            return dto;
        }).collect(Collectors.toList());
        return listDto;
    }
}
