package kafkagoal.paymentsprocessorservice.config;

import kafka_goal.api_payments_processor_service.model.Payment;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Configuration
public class KafkaConfig {

    @Value("${custom-kafka.payment-topic}")
    private String sourceTopic;

    @Value("${custom-kafka.consumer-notification-group}")
    private String notificationGroup;

    @Value("${custom-kafka.consumer-statistics-group}")
    private String statisticsGroup;

    @Bean
    public KafkaReceiver<UUID, Payment> reactiveKafkaConsumer(KafkaProperties kafkaProperties) {
        kafkaProperties.getConsumer().setGroupId(notificationGroup);
        ReceiverOptions<UUID, Payment> receiverOptions = ReceiverOptions.<UUID, Payment>create(kafkaProperties.buildConsumerProperties())
                .withValueDeserializer(getDeserializer())
                .subscription(Collections.singleton(sourceTopic));
        return KafkaReceiver.create(receiverOptions);
    }

//    @Bean(name = "notificationConsumer")
//    public ReactiveKafkaProducerTemplate<UUID, Payment> reactiveKafkaProducerTemplate(KafkaProperties kafkaProperties) {
//        SenderOptions<UUID, Payment> senderOptions = SenderOptions.create(kafkaProperties.buildProducerProperties());
//        return new ReactiveKafkaProducerTemplate<>(senderOptions);
//    }

    private JsonDeserializer<Payment> getDeserializer() {
        JsonDeserializer<Payment> deserializer = new JsonDeserializer<>(Payment.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
