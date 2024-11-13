package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.VarietyResponseDto;
import swp.koi.model.Variety;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VarietyEntityToDtoConverter {

    private final ModelMapper modelMapper;

    public List<VarietyResponseDto> convertVarietyList(List<Variety> varieties){
        List<VarietyResponseDto> response = varieties.stream()
                .map(variety -> modelMapper.map(variety, VarietyResponseDto.class))
                .collect(Collectors.toList());
        return response;
    }

}
