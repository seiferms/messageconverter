package digital.frontiers.messageconverters.configuration;

import com.google.zxing.WriterException;
import digital.frontiers.messageconverters.service.QRGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class StringQRCodeMessageConverter implements HttpMessageConverter<String> {

    @Autowired
    private QRGeneratorService qrGeneratorService;

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        // we dont read any incoming messages
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return clazz == String.class
                && MediaType.IMAGE_PNG.equalsTypeAndSubtype(mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.IMAGE_PNG);
    }

    @Override
    public String read(Class<? extends String> clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException, IOException {
        // we can safely return null since read is only ever called after canRead returned true.
        return null;
    }

    @Override
    public void write(String message, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            BufferedImage qrCode = qrGeneratorService.createQRCode(message, 500);
            outputMessage.getHeaders().setContentType(MediaType.IMAGE_PNG);

            if (outputMessage instanceof StreamingHttpOutputMessage) {
                StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
                streamingOutputMessage.setBody(outputStream -> writeBufferedImageAsContentTypeToOutputStream(qrCode, MediaType.IMAGE_PNG, outputStream));
            } else {
                writeBufferedImageAsContentTypeToOutputStream(qrCode, MediaType.IMAGE_PNG, outputMessage.getBody());
            }

        } catch (WriterException e) {
            throw new HttpMessageNotWritableException("error while writing the http message", e);
        }
    }

    private void writeBufferedImageAsContentTypeToOutputStream(BufferedImage image, MediaType contentType, OutputStream body)
            throws IOException, HttpMessageNotWritableException {

        ImageOutputStream imageOutputStream = null;
        ImageWriter imageWriter = null;
        try {
            Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(contentType.toString());
            if (imageWriters.hasNext()) {
                imageWriter = imageWriters.next();
                imageOutputStream = new MemoryCacheImageOutputStream(body);
                imageWriter.setOutput(imageOutputStream);
                imageWriter.write(null, new IIOImage(image, null, null), imageWriter.getDefaultWriteParam());
            } else {
                throw new HttpMessageNotWritableException(
                        "Could not find javax.imageio.ImageWriter for Content-Type [" + contentType + "]");
            }
        } finally {
            if (imageWriter != null) {
                imageWriter.dispose();
            }
            if (imageOutputStream != null) {
                try {
                    imageOutputStream.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

}
