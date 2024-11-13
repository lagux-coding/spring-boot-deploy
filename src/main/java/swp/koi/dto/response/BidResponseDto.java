package swp.koi.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BidResponseDto {

    Integer bidId;
    Integer lotId;
    float bidAmount;
    LocalDateTime bidTime;
    MemberResponseDto member;

}
