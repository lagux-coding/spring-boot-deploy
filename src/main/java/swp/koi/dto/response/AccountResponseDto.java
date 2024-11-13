package swp.koi.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.AccountRoleEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponseDto {

    Integer accountId;
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
    LocalDateTime createAt;
    AccountRoleEnum role;

}
