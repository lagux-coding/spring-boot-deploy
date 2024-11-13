package swp.koi.service.koiBreederService;

import jakarta.validation.Valid;
import swp.koi.dto.request.KoiBreederDto;
import swp.koi.dto.request.UpdateBreederProfileDto;
import swp.koi.dto.response.KoiBreederResponseDto;
import swp.koi.model.Account;
import swp.koi.model.KoiBreeder;

public interface KoiBreederService {
    KoiBreederResponseDto createKoiBreeder(KoiBreederDto request);

    KoiBreeder findByAccount(Account account);

    void updateBreederProfile(@Valid UpdateBreederProfileDto request);

    KoiBreeder getBreederInfo();
}
