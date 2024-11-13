package swp.koi.service.bidService;

import swp.koi.dto.request.AutoBidRequestDto;
import swp.koi.dto.request.BidRequestDto;
import swp.koi.exception.KoiException;
import swp.koi.model.Bid;

import java.util.List;
import java.util.Optional;

public interface BidService {

    void bid(BidRequestDto bidRequestDto);

    List<Bid> listBidByLotId(int lotId);

    void activeAutoBid(AutoBidRequestDto autoBidRequestDTO) throws KoiException;

    boolean isUserBidded(int lotId);

    Optional<Integer> countNumberOfPeopleWhoBidOnSpecificLot(int lotId);
}
