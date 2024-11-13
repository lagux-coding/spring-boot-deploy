package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.AuctionRequestResponseDto;
import swp.koi.model.AuctionRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuctionRequestEntityToDtoConverter {

    private final ModelMapper modelMapper;
    private final AccountEntityToDtoConverter accountEntityToDtoConverter;

    public AuctionRequestResponseDto convertAuctionRequest(AuctionRequest auctionRequest){
        AuctionRequestResponseDto response = modelMapper.map(auctionRequest, AuctionRequestResponseDto.class);
        return response;
    }

    public List<AuctionRequestResponseDto> convertAuctionRequestList(List<AuctionRequest> auctionRequests){
        List<AuctionRequestResponseDto> response = auctionRequests
                .stream()
                .filter(Objects::nonNull)
                .map(auctionRequest -> {
                    AuctionRequestResponseDto dto = modelMapper.map(auctionRequest, AuctionRequestResponseDto.class);
                    dto.getKoiFish().setAuctionTypeName(auctionRequest.getKoiFish().getAuctionType().getAuctionTypeName());
                    if(auctionRequest.getAccount() == null) {
                        dto.setStaff(null);
                    }else {
                        dto.setStaff(accountEntityToDtoConverter.convertAccount(auctionRequest.getAccount()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return response;
    }

}
