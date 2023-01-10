package kafkagoal.paymentsprocessorservice.service.consumers;

import kafka_goal.api_payments_processor_service.model.Payment;
import kafkagoal.paymentsprocessorservice.service.MailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;

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
        receiver.receiveAtmostOnce()
                .doOnNext(receiverRecord -> log.info("NotificationKafkaConsumer consume():  " + logSuccessResult(receiverRecord)))
                .doOnError(throwable -> log.error("NotificationKafkaConsumer consume(): Error of consuming kafka message, throwable = {}", throwable.getMessage()))
                .subscribe(consumerRecord -> {
                    var payment = consumerRecord.value();
                    mailSender.send(payment.getEmail(),"Payment notification", createTextMessage(payment));
                });
    }

    private String logSuccessResult(ConsumerRecord<UUID, Payment> receiverRecord) {
        return String.format("Successfully received kafka message. CorrelationId = %s, offset = %d, timestamp = %s",
                new String(receiverRecord.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value()),
                receiverRecord.offset(),
                new Timestamp(receiverRecord.timestamp()).toLocalDateTime()
        );
    }

    private String createTextMessage(Payment payment) {
        return String.format("Уважаемый %s, Вы совершили покупку в магазине \"%s\". Время платежа %s, cумма покупки %,.2f, остаток на счете %,.2f",
                payment.getClientName(), payment.getStore(), payment.getPaymentDate().toString(), payment.getAmountPayment(), payment.getCurrentBalance());
    }
}
