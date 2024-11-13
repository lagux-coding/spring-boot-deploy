package swp.koi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileDto {

    @NotBlank(message = "First Name is required")
    String firstName;

    @NotBlank(message = "Last Name is required")
    String lastName;

    @Pattern(regexp = "^\\+?[0-9]{10}$", message = "Phone number must be valid and contain 10 digits")
    String phoneNumber;

}
