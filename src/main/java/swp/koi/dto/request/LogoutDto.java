package swp.koi.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogoutDto {
    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;
}
