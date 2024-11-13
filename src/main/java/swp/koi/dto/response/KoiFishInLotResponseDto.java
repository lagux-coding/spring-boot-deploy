package swp.koi.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.AuctionTypeNameEnum;
import swp.koi.model.enums.KoiFishStatusEnum;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KoiFishInLotResponseDto {
    Integer fishId;
    String gender;
    int age;
    float size;
    float price;
    String imageUrl;
    String videoUrl;
    String varietyName;
    String breederName;
}
