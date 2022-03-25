package digital.frontiers.messageconverters.service;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

public class QRGeneratorServiceTest {

    @Test
    public void createSampleQRCode() throws WriterException {
        QRGeneratorService generatorService = new QRGeneratorService();
        BufferedImage hello_world = generatorService.createQRCode("hello world", 500);
        System.out.println();
    }
}