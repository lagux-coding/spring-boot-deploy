package swp.koi.service.lotService;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import swp.koi.dto.response.ResponseCode;
import swp.koi.dto.response.ResponseData;
import swp.koi.model.Invoice;
import swp.koi.model.Lot;
import swp.koi.repository.LotRepository;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test-invoice")
public class TestController {

    private final LotService lotService;
    private final LotRepository lotRepository;

    @GetMapping("/send-msg")
    public void sendMsg(@RequestParam int lotId) {

        Optional<Lot> lot = lotRepository.findById(lotId);

        lotService.sendNotificateToFollower(lot.get());
    }

//    @GetMapping("/invoice")
//    public ResponseData<?> createInvoice(@RequestParam int lotId,
//                                         @RequestParam int memberId) throws UnsupportedEncodingException {
//        Invoice lotINvoice = lotService.generateInvoice(lotId, memberId);
//
//        return new ResponseData<>(ResponseCode.SUCCESS, lotINvoice);
//    }
}
