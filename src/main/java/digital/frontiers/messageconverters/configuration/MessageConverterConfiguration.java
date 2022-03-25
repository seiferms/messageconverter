package digital.frontiers.messageconverters.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class MessageConverterConfiguration {

    @Bean
    public HttpMessageConverter<String> stringToQrCodeGenerator() {
        return new StringQRCodeMessageConverter();
    }

}
