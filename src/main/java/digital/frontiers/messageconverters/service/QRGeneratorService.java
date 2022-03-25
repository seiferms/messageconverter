package digital.frontiers.messageconverters.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class QRGeneratorService {

    public BufferedImage createQRCode(String text, int edgeLength) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix encode = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, edgeLength, edgeLength);
        return MatrixToImageWriter.toBufferedImage(encode);
    }

}
