package swp.koi.service.excelService;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelGenerateService {

    public <T> ByteArrayOutputStream generateExcel(List<T> listOfObjectConvertToExcel, ExportDataToExcel<T> exportType) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        int columnIdx = 0;

        // Create headers
        Map<String, String> columnData = exportType.getColumnsData();
        for (String header : columnData.values()) {
            Cell cell = headerRow.createCell(columnIdx++);
            cell.setCellValue(header);
        }

        // Fill data rows
        List<Map<String, Object>> listOfData = exportType.prepareDataForUse(listOfObjectConvertToExcel);
        int rowIdx = 1;

        for (Map<String, Object> map : listOfData) {
            Row row = sheet.createRow(rowIdx++);
            columnIdx = 0;

            for (String key : map.keySet()) {
                Cell cell = row.createCell(columnIdx++);
                Object value = map.get(key);
                if (value != null) {
                    cell.setCellValue(value.toString());
                }
            }
        }

        // Write the output to a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }
}
