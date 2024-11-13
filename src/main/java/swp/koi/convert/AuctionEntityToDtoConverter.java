package swp.koi.convert;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import swp.koi.dto.response.AuctionResponseDto;
import swp.koi.model.Auction;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuctionEntityToDtoConverter {

    private final ModelMapper modelMapper;
    private final LotEntityToDtoConverter lotEntityToDtoConverter;

    public List<AuctionResponseDto> converAuctiontList(List<Auction> auctions){
        List<AuctionResponseDto> response = auctions
                .stream()
                .map(auction -> {
                    AuctionResponseDto dto = modelMapper.map(auction, AuctionResponseDto.class);
                    dto.setLots(lotEntityToDtoConverter.convertLotList(auction.getLots()));
                    return dto;
                }).collect(Collectors.toList());
        return response;
    }

    public AuctionResponseDto convertAuction(Auction auction){
        AuctionResponseDto response = modelMapper.map(auction, AuctionResponseDto.class);
        response.setLots(lotEntityToDtoConverter.convertLotList(auction.getLots()));
        return response;
    }

}
