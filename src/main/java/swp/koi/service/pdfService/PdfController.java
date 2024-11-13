package swp.koi.service.pdfService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.koi.model.Invoice;
import swp.koi.repository.InvoiceRepository;

import java.io.IOException;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    private final InvoiceRepository invoiceRepository;

    @GetMapping("/generate-pdf")
    public ResponseEntity<byte[]> exportInvoice() throws IOException {
        String[] items = {"Koi Fish (Type C)", "Koi Fish (Type B)"};
        int[] quantities = {2, 1};
        double[] prices = {500.00, 300.00};
        double totalAmount = 500.00;

        Invoice invoice = invoiceRepository.findById(12).orElseThrow(() -> new RuntimeException("Invoice not found"));

        byte[] pdfBytes = pdfService.exportInvoicePdf(invoice);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}
