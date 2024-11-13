package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.KoiFishResponseDto;
import swp.koi.model.KoiFish;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KoiFishEntityToDtoConverter {

    private final ModelMapper modelMapper;

    public List<KoiFishResponseDto> convertFishList(List<KoiFish> koiFishes){
        List<KoiFishResponseDto> response = koiFishes.stream()
                .map(koiFish -> modelMapper.map(koiFish, KoiFishResponseDto.class))
                .collect(Collectors.toList());
        return response;
    }

}
