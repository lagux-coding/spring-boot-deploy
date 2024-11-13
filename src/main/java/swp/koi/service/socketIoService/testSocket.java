package swp.koi.service.socketIoService;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class testSocket {

    private final EventListenerFactoryImpl eventListenerFactory;
    private final SocketIOServer socketIOServer;

    @GetMapping("/create")
    public void createSocketIo(@RequestParam String port) {
//        eventListenerFactory.createDataListener(socketIOServer, port);
    }

    @PostMapping("/send-data")
    public void sendData(@RequestBody SocketDetail socketDetail, String event){
        eventListenerFactory.sendDataToClient(socketDetail, event);
    }
}
