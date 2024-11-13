package swp.koi.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.AuctionRequestStatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRequestResponseDto {

    Integer requestId;
    AuctionRequestStatusEnum status;
    LocalDateTime requestedAt;
    String auctionTypeName;
    float offerPrice;
    Float auctionFinalPrice;
    KoiFishResponseDto KoiFish;
    KoiBreederResponseDto breeder;
    AccountResponseDto staff;

}
