package kafkagoal.paymentsprocessorservice.service.consumers;

import kafka_goal.api_payments_processor_service.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@Component("statisticsConsumer")
@RequiredArgsConstructor
public class StatisticsKafkaConsumer implements KafkaConsumer {
    private final ReactiveKafkaConsumerTemplate<UUID, Payment> consumerTemplate;

    @Override
    public Flux<Payment> consume() {
        return consumerTemplate.receive()
                .doOnNext(receivedRecord -> log.info("NotificationKafkaConsumer consume():  " + logSuccessResult(receivedRecord)))
                .doOnError(throwable -> log.error("NotificationKafkaConsumer consume(): Error of consuming kafka message, throwable = {}", throwable.getMessage()))
                .map(receiverRecord -> {
                    receiverRecord.receiverOffset().acknowledge();
                    return receiverRecord.value();
                });
    }
}
