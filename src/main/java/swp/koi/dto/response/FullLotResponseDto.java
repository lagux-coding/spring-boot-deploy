package swp.koi.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.LotStatusEnum;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullLotResponseDto {

    Integer lotId;
    float deposit;
    float startingPrice;
    float increment;
    Integer currentMemberId;
    float currentPrice;
    LotStatusEnum status;
    KoiFishResponseDto koiFish;
    AuctionTypeResponseDto auctionType;

}
