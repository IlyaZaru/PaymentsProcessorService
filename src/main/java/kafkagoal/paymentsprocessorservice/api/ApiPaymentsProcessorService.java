package kafkagoal.paymentsprocessorservice.api;

import kafkagoal.paymentsprocessorservice.service.consumers.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class ApiPaymentsProcessorService {
    @Qualifier("notificationConsumer")
    @Autowired
    private KafkaConsumer notificationConsumer;

    @PostConstruct
    private void listenByNotificationConsumer() {
        notificationConsumer.consume();
    }
}
