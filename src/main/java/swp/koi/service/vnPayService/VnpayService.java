package swp.koi.service.vnPayService;

import jakarta.servlet.http.HttpServletRequest;
import swp.koi.model.enums.TransactionTypeEnum;

import java.io.UnsupportedEncodingException;

public interface VnpayService {

    public String generateInvoice(int registerLot, int memberId, TransactionTypeEnum transactionTypeEnum) throws UnsupportedEncodingException;

    public boolean isResponseValid(HttpServletRequest request) throws UnsupportedEncodingException;

    public void handlePayment(HttpServletRequest request) throws UnsupportedEncodingException;
}
