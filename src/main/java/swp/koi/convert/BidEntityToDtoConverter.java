package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.BidResponseDto;
import swp.koi.dto.response.MemberResponseDto;
import swp.koi.model.Bid;

import java.util.List;
import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
public class BidEntityToDtoConverter {

    private final ModelMapper modelMapper;
    private final AccountEntityToDtoConverter accountEntityToDtoConverter;

    public List<BidResponseDto> convertBidList(List<Bid> bids){
        List<BidResponseDto> response = bids
                .stream()
                .map(bid -> {
                    BidResponseDto dto = modelMapper.map(bid, BidResponseDto.class);
                    MemberResponseDto memberDto = new MemberResponseDto();
                    memberDto.setAccount(accountEntityToDtoConverter
                            .convertAccount(bid.getMember().getAccount()));
                    memberDto.setMemberId(bid.getMember().getMemberId());
                    dto.setMember(memberDto);
                    return dto;
                })
                .collect(Collectors.toList());
        return response;
    }

}
