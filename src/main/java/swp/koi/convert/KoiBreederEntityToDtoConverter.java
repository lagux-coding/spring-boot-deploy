package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import swp.koi.dto.response.KoiBreederResponseDto;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.KoiBreeder;

@Configuration
@RequiredArgsConstructor
public class KoiBreederEntityToDtoConverter {

    private final ModelMapper modelMapper;

    public KoiBreederResponseDto convertBreeder(KoiBreeder koiBreeder){
        if(koiBreeder == null)
            throw new KoiException(ResponseCode.FAIL);
        return modelMapper.map(koiBreeder, KoiBreederResponseDto.class);
    }
}
