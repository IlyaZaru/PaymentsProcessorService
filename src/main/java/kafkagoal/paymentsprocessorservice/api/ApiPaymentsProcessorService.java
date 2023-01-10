package kafkagoal.paymentsprocessorservice.api;

import kafkagoal.paymentsprocessorservice.service.consumers.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class ApiPaymentsProcessorService {
    @Value("${mail-sender.enabled}")
    private boolean senderIsEnabled;

    @Qualifier("notificationConsumer")
    @Autowired
    private KafkaConsumer notificationConsumer;

    @PostConstruct
    private void listenByNotificationConsumer() {
        if (senderIsEnabled) {
            notificationConsumer.consume();
        }
    }
}
