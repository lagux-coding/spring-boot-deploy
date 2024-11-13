package swp.koi.service.pdfService;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/qr-code")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeGeneratorService qrCodeGeneratorService;

    @GetMapping("/qr-code")
    public ResponseEntity<byte[]> generateQRCode(@RequestParam String link) throws Exception {
        String url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=10000000&vnp_BankCode=NCB&vnp_Command=pay&vnp_CreateDate=20241016161127&vnp_CurrCode=VND&vnp_ExpireDate=20241016164127&vnp_IpAddr=127.0.0.1&vnp_Locale=vn&vnp_OrderInfo=memberid%3D1%26registerlot%3D46%26type%3DDEPOSIT&vnp_OrderType=other&vnp_ReturnUrl=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fpay%2Fvn-pay-callback&vnp_TmnCode=58X4B4HP&vnp_TxnRef=68791480&vnp_Version=2.1.0&vnp_SecureHash=485d7ed13423d9cc51d66df63a0f9a658c18e87ea1f1ba2e7affd876568a90eb387d30384ac65ad6776af1de4b3879fbece46b404f71dff4cafb632d2362ea03";

        int width = 300;
        int height = 300;

        byte[] qrCode = QrCodeGeneratorService.generateQRCode(link, width, height);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
    }

}
