package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.MemberResponseDto;
import swp.koi.model.Member;


@Component
@RequiredArgsConstructor
public class MemberEntityToDtoConverter {
    private final ModelMapper modelMapper;

    public MemberResponseDto convertMember(Member member){
        MemberResponseDto dto = modelMapper.map(member, MemberResponseDto.class);
        return dto;
    }
}
