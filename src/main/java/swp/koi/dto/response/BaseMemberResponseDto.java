package swp.koi.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseMemberResponseDto extends AccountResponseDto {
    Integer accountId;
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
}
