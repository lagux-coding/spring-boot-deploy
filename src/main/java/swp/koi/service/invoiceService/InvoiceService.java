package swp.koi.service.invoiceService;

import swp.koi.dto.response.AuctionedFishPricesResponseDto;
import swp.koi.dto.response.InvoiceResponseDto;
import swp.koi.model.Invoice;
import swp.koi.model.enums.InvoiceStatusEnums;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface InvoiceService {

    Invoice createInvoiceForAuctionWinner(int lotId, int memberId);

    void updateStatusOfInvoice();

    String regeneratePaymentLinkForInvoice(int invoiceId) throws UnsupportedEncodingException;

    List<Invoice> getAllInvoicesForAuctionWinner();

    Invoice updateInvoiceAddress(float kilometer, int invoiceId, String address) throws UnsupportedEncodingException;

    Invoice getInvoiceForSpecificLot(int lotId);

    void assignStaffDelivery(Integer invoiceId, Integer accountId);

    List<Invoice> getAllDeliveringInvoices();

    List<Invoice> listOfInvoices();

    void updateInvoiceStatus(Integer invoiceId, InvoiceStatusEnums status);

    List<Invoice> listAllInvoicesForManager();

    List<Invoice> getAllInvoices();

    List<AuctionedFishPricesResponseDto> getAllAuctionedFishPrices();
}
