package swp.koi.service.pdfService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import swp.koi.model.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class PdfService {

    public byte[] exportInvoicePdf(Invoice invoice) throws IOException {


        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Format date and amounts
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedInvoiceDate = invoice.getInvoiceDate().format(dateFormatter);
            String formattedDueDate = invoice.getDueDate() != null ? invoice.getDueDate().format(dateFormatter) : "N/A";
            String formattedFinalAmount = String.format("%.2f", invoice.getFinalAmount());
            String formattedSubTotal = String.format("%.2f", invoice.getSubTotal());
            String formattedTax = String.format("%.2f", invoice.getTax());
            String depositCost = String.valueOf(invoice.getLot().getDeposit());

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {


                // Invoice Header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("INVOICE");


                contentStream.endText();

                // Draw a line under the header
                contentStream.moveTo(50, 745);
                contentStream.lineTo(550, 745);
                contentStream.stroke();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 730);

                // Invoice Details Section
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Invoice Details:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLine();
                contentStream.showText("Invoice ID: " + invoice.getInvoiceId());
                contentStream.newLine();
                contentStream.showText("Invoice Date: " + formattedInvoiceDate);
                contentStream.newLine();
                contentStream.showText("Due Date: " + formattedDueDate);
                contentStream.newLine();
                contentStream.showText("Status: " + invoice.getStatus().toString());
                contentStream.newLine();
                contentStream.newLine();

                // Customer Details Section
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Customer Details:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLine();

                contentStream.showText("Winner Email: " + invoice.getMember().getAccount().getEmail());
                contentStream.newLine();
                contentStream.newLine();

                // Lot Details Section
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Lot Details:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLine();
                contentStream.showText("Lot ID: " + invoice.getLot().getLotId());
                contentStream.newLine();
                contentStream.newLine();

                // Payment Information Section
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Payment Information:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLine();
                contentStream.showText("Final Amount: " + formattedFinalAmount + " vnd");
                contentStream.newLine();
                contentStream.showText("SubTotal: " + formattedSubTotal + " vnd");
                contentStream.newLine();
                contentStream.showText("Tax: " + formattedTax + " vnd");
                contentStream.newLine();
                contentStream.showText("Tax: " + depositCost + " vnd");
                contentStream.newLine();
                contentStream.newLine();

                // Payment Link
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Payment Link:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLine();
                contentStream.showText(invoice.getPaymentLink() != null ? invoice.getPaymentLink() : "N/A");
                contentStream.endText();
            }

            // Save document to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
