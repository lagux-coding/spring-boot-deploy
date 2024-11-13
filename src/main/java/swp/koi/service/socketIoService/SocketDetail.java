package swp.koi.service.socketIoService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocketDetail {
    private String winnerName;
    private float newPrice;
    private int lotId;

}
