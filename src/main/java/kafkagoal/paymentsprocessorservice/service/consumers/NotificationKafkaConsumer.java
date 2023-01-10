package kafkagoal.paymentsprocessorservice.service.consumers;

import kafka_goal.api_payments_processor_service.model.Payment;
import kafkagoal.paymentsprocessorservice.service.MailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.sql.Timestamp;
import java.util.UUID;
@Slf4j
@Component("notificationConsumer")
@RequiredArgsConstructor
public class NotificationKafkaConsumer implements KafkaConsumer {
    private final KafkaReceiver<UUID, Payment> receiver;
    private final MailSender mailSender;

    @Override
    public void consume() {
        receiver.receive()
                .doOnNext(receiverRecord -> log.info("NotificationKafkaConsumer consume():  " + logSuccessResult(receiverRecord)))
                .doOnError(throwable -> log.error("NotificationKafkaConsumer consume(): Error of consuming kafka message, throwable = {}", throwable.getMessage()))
                .subscribe(receiverRecord -> {
                    mailSender.send(receiverRecord.value());
                });
    }

    private String logSuccessResult(ReceiverRecord<UUID, Payment> receiverRecord) {
        return String.format("Successfully received kafka message. CorrelationId = %s, offset = %d, timestamp = %s",
                new String(receiverRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value()),
                receiverRecord.offset(),
                new Timestamp(receiverRecord.timestamp()).toLocalDateTime()
        );
    }
}
