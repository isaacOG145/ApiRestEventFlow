package utez.edu.ApiRestEventFlow.qr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QRCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCode(@RequestParam String text) {
        try {
            byte[] qrCode = qrCodeService.generateQRCode(text, 250, 250);
            return ResponseEntity.ok().body(qrCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}