package swp.koi.service.varietyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.koi.convert.VarietyEntityToDtoConverter;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.Variety;
import swp.koi.repository.VarietyRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VarietyServiceImpl implements VarietyService{

    private final VarietyRepository varietyRepository;

    @Override
    public Variety findByVarietyName(String name) {
        return varietyRepository.findByVarietyName(name).orElseThrow(() -> new KoiException(ResponseCode.VARIETY_NOT_FOUND));
    }

    @Override
    public List<Variety> getAllVariety() {
        return varietyRepository.findAll();
    }
}
