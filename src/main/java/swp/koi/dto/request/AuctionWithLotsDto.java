package swp.koi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionWithLotsDto {

    @NotNull(message = "Start time can not be null")
    java.time.LocalDateTime startTime;
    @NotNull(message = "End time can not be null")
    java.time.LocalDateTime endTime;
    List<LotDto> lots;

}
