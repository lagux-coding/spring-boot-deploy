package swp.koi.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.LotRegisterStatusEnum;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LotRegisterForMemberResponseDto {

    LotRegisterStatusEnum status;
    Integer auctionId;
    LotResponseDto lot;

}
