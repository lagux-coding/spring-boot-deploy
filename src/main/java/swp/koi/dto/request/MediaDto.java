package swp.koi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaDto {
    @NotBlank(message = "Image url is required")
    String imageUrl;
    @NotBlank(message = "Video url is required")
    String videoUrl;
}
