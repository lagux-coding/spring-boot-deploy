package swp.koi.service.varietyService;

import swp.koi.model.Variety;

import java.util.List;

public interface VarietyService {
    Variety findByVarietyName(String name);

    List<Variety> getAllVariety();
}
