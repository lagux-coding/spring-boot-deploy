package swp.koi.service.transactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.*;
import swp.koi.model.enums.TransactionTypeEnum;
import swp.koi.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final LotRepository lotRepository;
    private final MemberRepository memberRepository;
    private final InvoiceRepository invoiceRepository;
    private final AuctionRequestRepository auctionRequestRepository;

    @Override
    public Transaction createTransactionForLotDeposit(int lotId, int memberId) {
        Lot lot = getLot(lotId);
        Member member = getMember(memberId);

        Transaction transaction = buildTransactionForDeposit(lot, member);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction createTransactionForInvoicePayment(int lotId, int memberId) {
        Lot lot = getLot(lotId);
        Member member = getMember(memberId);
        Invoice invoice = invoiceRepository.findByLot(lot);

        Transaction transaction = buildTransactionForInvoicePayment(lot, member, invoice);
        return transactionRepository.save(transaction);
    }

    @Override
    public void createTransactionForRefund(LotRegister lotRegister){
        Transaction transaction = buildTransactionForRefund(lotRegister);
        transactionRepository.save(transaction);
    }

    @Override
    public void createTransactionForBreederPayment(Integer auctionRequestId){

        AuctionRequest auctionRequest = auctionRequestRepository.findByRequestId(auctionRequestId)
                .orElseThrow(() -> new KoiException(ResponseCode.AUCTION_REQUEST_NOT_FOUND));

        Transaction transaction = buildTransactionForBreederPayment(auctionRequest);
        transactionRepository.save(transaction);
    }

    private Lot getLot(int lotId) {
        return lotRepository.findById(lotId)
                .orElseThrow(() -> new KoiException(ResponseCode.LOT_NOT_FOUND));
    }

    private Member getMember(int memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KoiException(ResponseCode.MEMBER_NOT_FOUND));
    }

    private Transaction buildTransactionForDeposit(Lot lot, Member member) {
        return Transaction.builder()
                .transactionType(TransactionTypeEnum.DEPOSIT)
                .amount(lot.getDeposit())
                .member(member)
                .paymentStatus("SUCCESS")
                .lot(lot)
                .build();
    }

    private Transaction buildTransactionForInvoicePayment(Lot lot, Member member, Invoice invoice) {
        return Transaction.builder()
                .transactionType(TransactionTypeEnum.INVOICE_PAYMENT)
                .amount(invoice.getFinalAmount())
                .member(member)
                .paymentStatus("SUCCESS")
                .lot(lot)
                .invoice(invoice)
                .build();
    }


    @Override
    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    private Transaction buildTransactionForRefund(LotRegister lotRegister) {
        return Transaction.builder()
                .transactionType(TransactionTypeEnum.REFUND)
                .amount(-lotRegister.getDeposit())
                .lot(lotRegister.getLot())
                .member(lotRegister.getMember())
                .paymentStatus("SUCCESS")
                .build();
    }

    private Transaction buildTransactionForBreederPayment(AuctionRequest auctionRequest) {
        return Transaction.builder()
                .transactionType(TransactionTypeEnum.PAYMENT_FOR_BREEDER)
                .amount(-auctionRequest.getAuctionFinalPrice())
                .breeder(auctionRequest.getKoiBreeder())
                .paymentStatus("SUCCESS")
                .build();
    }
}