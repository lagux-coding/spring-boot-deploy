package swp.koi.service.auctionTypeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.koi.dto.response.ResponseCode;
import swp.koi.exception.KoiException;
import swp.koi.model.AuctionType;
import swp.koi.model.enums.AuctionTypeNameEnum;
import swp.koi.repository.AuctionTypeRepository;

@Service
@RequiredArgsConstructor
public class AuctionTypeServiceImpl implements AuctionTypeService{

    private final AuctionTypeRepository auctionTypeRepository;

    @Override
    public AuctionType findByAuctionTypeName(String auctionTypeName) throws KoiException{
        AuctionTypeNameEnum auctionTypeEnum;
        try{
            auctionTypeEnum = AuctionTypeNameEnum.valueOf(auctionTypeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new KoiException(ResponseCode.AUCTION_TYPE_NOT_FOUND);
        }
        AuctionType auctionType = auctionTypeRepository.findByAuctionTypeName(auctionTypeEnum).orElseThrow(() -> new KoiException(ResponseCode.AUCTION_TYPE_NOT_FOUND));
        return auctionType  ;
    }
}
