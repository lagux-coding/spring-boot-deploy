package swp.koi.service.koiFishService;

import swp.koi.dto.request.*;
import swp.koi.model.KoiFish;

import java.util.List;

public interface KoiFishService {
    KoiFish createKoiFishFromRequest(KoiFishDto koiRequest, MediaDto mediaRequest);
    KoiFish findByFishId(Integer fishId);

    List<KoiFish> getKoiFishFromRegisteredRequest();

    void saveFish(KoiFish koiFish);

    List<KoiFish> getKoiFishBasedOnType(AuctionTypeDto auctionTypeDTO);
}
