package kafkagoal.paymentsprocessorservice.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.io.IOException;
import java.time.OffsetDateTime;

@Configuration
public class WebFluxConfig {

    public static class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
        @Override
        public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                gen.writeString(value.toString());
            } else {
                gen.writeNull();
            }
        }
    }


    public static class CustomTimeModule extends SimpleModule {
        private static final long serialVersionUID = 1L;

        public CustomTimeModule() {
            super(new Version(1, 0, 0, "RELEASE", WebFluxConfig.class.getPackageName(), CustomTimeModule.class.getName()));

            addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        }
    }

    @Bean
    Jackson2JsonEncoder jackson2JsonEncoder(){
        ObjectMapper responseObjectMapper = new ObjectMapper().registerModule(new CustomTimeModule());
        return new Jackson2JsonEncoder(responseObjectMapper);
    }

    @Bean
    WebFluxConfigurer webFluxConfigurer(){
        return new WebFluxConfigurer() {
            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer.defaultCodecs().jackson2JsonEncoder(jackson2JsonEncoder());
            }
        };
    }
}
