package swp.koi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KoiBreederDto {

    @NotBlank(message = "Breeder name is required")
    private String breederName;

    @NotBlank(message = "Location is required")
    private String location;

    @Valid
    private AccountRegisterDto account;
}
