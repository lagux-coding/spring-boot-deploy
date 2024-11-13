package swp.koi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KoiFishUpdateDto {

    @NotBlank(message = "Variety name is required")
    private String varietyName;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|UNKNOWN)$", message = "Gender must be either MALE, FEMALE and UNKNOWN")
    private String gender;

    @Min(value = 0, message = "Age must be greater than or equal to 0")
    private int age;

    @Positive(message = "Size must be a positive number")
    private float size;

    @Positive(message = "Price must be a positive number")
    private float price;

    String auctionTypeName;

    private MediaUpdateDto media;
}

