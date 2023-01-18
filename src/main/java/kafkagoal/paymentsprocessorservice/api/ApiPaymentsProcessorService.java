package kafkagoal.paymentsprocessorservice.api;

import kafka_goal.api_payments_processor_service.api.PaymentProcessorServiceApi;
import kafka_goal.api_payments_processor_service.model.ClientInfo;
import kafka_goal.api_payments_processor_service.model.PaymentsStatistic;
import kafkagoal.paymentsprocessorservice.service.StatisticsComputer;
import kafkagoal.paymentsprocessorservice.service.consumers.KafkaConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequiredArgsConstructor
public class ApiPaymentsProcessorService implements PaymentProcessorServiceApi {
    @Value("${mail-sender.enabled}")
    private boolean senderIsEnabled;
    private final KafkaConsumer notificationConsumer;
    private final StatisticsComputer statisticsComputer;

    @PostConstruct
    private void listenByNotificationConsumer() {
        if (senderIsEnabled) {
            notificationConsumer.consume();
        }
    }

    @Override
    public Mono<ResponseEntity<PaymentsStatistic>> paymentsStatisticsPost(Mono<ClientInfo> clientInfo, ServerWebExchange exchange) {
        final AtomicReference<UUID> requestIdRef = new AtomicReference<>(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        final AtomicReference<UUID> clientIdRef = new AtomicReference<>(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        final AtomicReference<String> storeRef = new AtomicReference<>(null);
        return statisticsComputer.compute(clientInfo.doOnNext(clInfo -> {
                    requestIdRef.set(clInfo.getRequestId());
                    clientIdRef.set(clInfo.getClientId());
                    storeRef.set(clInfo.getStore());
                }))
                .map(paymentsStatistic -> {
                    paymentsStatistic.setRequestId(requestIdRef.get());
                    paymentsStatistic.setFiltrationStore(storeRef.get());
                    return ResponseEntity.ok(paymentsStatistic);
                })
                .onErrorResume(throwable -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PaymentsStatistic()
                        .clientId(clientIdRef.get())
                        .requestId(requestIdRef.get())
                        .filtrationStore(storeRef.get())
                        .errorMessage(throwable.getMessage()))));
    }
}
