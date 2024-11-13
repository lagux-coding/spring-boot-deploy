package swp.koi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.koi.model.Auction;
import swp.koi.model.AuctionType;
import swp.koi.model.Lot;
import swp.koi.model.enums.AuctionTypeNameEnum;
import swp.koi.model.enums.LotStatusEnum;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {

    List<Lot> findAllByStatusAndStartingTimeLessThan(LotStatusEnum status, LocalDateTime startingTime);

    List<Lot> findAllByStatusAndEndingTimeLessThan(LotStatusEnum status, LocalDateTime endingTime);

    List<Lot> findAllByStatusAndAuctionType(LotStatusEnum status, AuctionType auctionType);

    List<Lot> findAllByAuction(Auction auction);
}
