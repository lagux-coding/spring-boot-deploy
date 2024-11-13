package swp.koi.service.auctionService;

import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Scheduled;
import swp.koi.dto.request.AuctionWithLotsDto;
import swp.koi.dto.response.AuctionResponseDto;
import swp.koi.model.Auction;
import swp.koi.model.Lot;

import java.util.List;

public interface AuctionService {
    @Scheduled(fixedDelay = 1000 * 60)
    void updateAuctionStatusAndEndTime();

    AuctionResponseDto createAuctionWithLots(@Valid AuctionWithLotsDto request);

    Lot getLot(Integer lotId);

    List<Auction> getAllAuction();

    Auction getAuction(Integer auctionId);

    List<Auction> getAllOnGoingAuction();

    List<Auction> getAllCompletedAuction();

    List<Auction> getAllWaitingAuction();
}
