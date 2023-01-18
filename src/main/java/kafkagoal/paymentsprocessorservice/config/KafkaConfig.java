package kafkagoal.paymentsprocessorservice.config;

import kafka_goal.api_payments_processor_service.model.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;

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
                .subscription(Collections.singleton(sourceTopic));
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<UUID, Payment> reactiveKafkaConsumerTemplate(KafkaProperties kafkaProperties) {
        kafkaProperties.getConsumer().setGroupId(statisticsGroup);
        ReceiverOptions<UUID, Payment> receiverOptions = ReceiverOptions.<UUID, Payment>create(kafkaProperties.buildConsumerProperties())
                .addAssignListener(partitions -> partitions.forEach(ReceiverPartition::seekToBeginning))
                .subscription(Collections.singleton(sourceTopic));
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
