package swp.koi.dto.request;


import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoBidRequestDto {
    @Min(value = 0,message = "Lot id must be greater than 0 or positive")
    private int lotId;

    @Min(value = 0,message = "Lot id must be greater than 0 or positive")
    private float amount;

}
