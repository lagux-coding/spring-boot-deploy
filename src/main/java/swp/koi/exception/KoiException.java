package swp.koi.exception;

import lombok.*;
import swp.koi.dto.response.ResponseCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Builder
public class KoiException extends RuntimeException {
    private ResponseCode responseCode;

    public KoiException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

}
