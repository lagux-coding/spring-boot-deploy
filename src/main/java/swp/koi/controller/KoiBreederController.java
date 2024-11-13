package swp.koi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import swp.koi.convert.KoiBreederEntityToDtoConverter;
import swp.koi.dto.response.KoiBreederResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;
import swp.koi.service.koiBreederService.KoiBreederService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/breeder")
@Tag(name = "breeder", description = "Everything about your breeder")
public class KoiBreederController {

    private final KoiBreederService koiBreederService;
    private final KoiBreederEntityToDtoConverter koiBreederEntityToDtoConverter;

    @GetMapping("/get-breeder-information")
    public ResponseData<KoiBreederResponseDto> getBreederInfo(){
        KoiBreederResponseDto response = koiBreederEntityToDtoConverter.convertBreeder(koiBreederService.getBreederInfo());
        return new ResponseData<>(ResponseCode.SUCCESS, response);
    }

}
