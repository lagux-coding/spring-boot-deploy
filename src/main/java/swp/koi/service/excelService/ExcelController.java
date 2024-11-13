package swp.koi.service.excelService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swp.koi.model.Invoice;
import swp.koi.repository.InvoiceRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelGenerateService generateService;
    private final InvoiceRepository invoiceRepository;
    private final ExcelGenerateService excelGenerator;
    @GetMapping("/invoice")

    public ResponseEntity<byte[]> generateExcel() throws IOException {
        List<Invoice> invoices = invoiceRepository.findAll();
        ExportDataToExcel<Invoice> exportType = new ExportInvoiceToExcel();
        ByteArrayOutputStream excelStream = excelGenerator.generateExcel(invoices, exportType);
        HttpHeaders headers = new HttpHeaders();
        byte[] excelBytes = excelStream.toByteArray();
        headers.add("Content-Disposition", "attachment; filename=sample.xlsx");

//
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

    }

}
