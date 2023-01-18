package kafkagoal.paymentsprocessorservice.service.consumers;

import kafka_goal.api_payments_processor_service.model.Payment;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import reactor.core.publisher.Flux;

import java.sql.Timestamp;
import java.util.UUID;

public interface KafkaConsumer {
    Flux<Payment> consume();

    default String logSuccessResult(ConsumerRecord<UUID, Payment> receiverRecord) {
        return String.format("Successfully received kafka message. CorrelationId = %s, offset = %d, timestamp = %s",
                new String(receiverRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value()),
                receiverRecord.offset(),
                new Timestamp(receiverRecord.timestamp()).toLocalDateTime()
        );
    }
}
