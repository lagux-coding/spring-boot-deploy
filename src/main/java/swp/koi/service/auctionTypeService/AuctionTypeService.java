package swp.koi.service.auctionTypeService;

import swp.koi.model.AuctionType;

public interface AuctionTypeService {
    AuctionType findByAuctionTypeName(String auctionTypeName);
}
