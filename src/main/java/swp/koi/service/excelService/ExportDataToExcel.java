package swp.koi.service.excelService;

import java.util.List;
import java.util.Map;

public interface ExportDataToExcel<T> {

    List<Map<String, Object>> prepareDataForUse(List<T> dataList);

    Map<String, String> getColumnsData();
}
