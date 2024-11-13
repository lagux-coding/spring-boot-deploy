package swp.koi.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import swp.koi.model.enums.TransactionTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionResponseDto {

    Integer transactionId;
    LocalDateTime transactionDate;
    TransactionTypeEnum transactionType;
    float amount;
    String paymentStatus;
//    LotResponseDto lot;
    Integer lotId;
    MemberResponseDto member;
    KoiBreederResponseDto koiBreeder;
}
