package swp.koi.service.excelService;

import swp.koi.model.Invoice;
import swp.koi.model.Transaction;
import swp.koi.service.excelService.ExportDataToExcel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExportInvoiceToExcel implements ExportDataToExcel<Invoice> {

    @Override
    public List<Map<String, Object>> prepareDataForUse(List<Invoice> dataList) {


        return dataList.stream()
                .map(data ->
                {
                    // Safely extract transaction ID
                    Integer transactionId = Optional.ofNullable(data.getTransaction())
                            .map(Transaction::getTransactionId)
                            .orElse(0);



                    return Map.of(
                            "id", data.getInvoiceId(),
                            "total", data.getFinalAmount(),
                            "startDate", data.getInvoiceDate(),
                            "endDate", data.getDueDate(),
                            "subTotal", data.getSubTotal(),
                            "tax", data.getTax(),
                            "status", data.getStatus(),
                            "member", Optional.ofNullable(data.getMember().getMemberId()),
                            "lot", data.getLot().getLotId(),
                            "transaction", transactionId // Use the safely extracted transaction ID
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getColumnsData() {
        return Map.of(
                "id", "Invoice ID",
                "total", "Total Amount",
                "startDate", "Start Date",
                "endDate", "End Date",
                "subTotal", "Subtotal",
                "tax", "Tax",
                "status", "Status",
                "member", "Member",
                "lot", "Lot ID",
                "transaction", "Transaction ID"
        );
    }
}