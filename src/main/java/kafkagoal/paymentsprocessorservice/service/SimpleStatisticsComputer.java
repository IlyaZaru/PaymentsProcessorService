package kafkagoal.paymentsprocessorservice.service;

import kafka_goal.api_payments_processor_service.model.ClientInfo;
import kafka_goal.api_payments_processor_service.model.Payment;
import kafka_goal.api_payments_processor_service.model.PaymentsStatistic;
import kafkagoal.paymentsprocessorservice.service.consumers.KafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Comparator;

@Slf4j
@Component
public class SimpleStatisticsComputer implements StatisticsComputer {
    private final KafkaConsumer kafkaConsumer;

    @Value("${custom-kafka.disconnect-timeout-statistic-receiver}")
    private long disconnectTimeout;

    public SimpleStatisticsComputer(@Qualifier("statisticsConsumer") KafkaConsumer kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

    @Override
    public Mono<PaymentsStatistic> compute(Mono<ClientInfo> clientInfoMono) {

        return clientInfoMono
                .flatMapMany(clientInfo -> kafkaConsumer.consume()
                        .filter(payment -> payment.getClientId().equals(clientInfo.getClientId()))
                        .filter(payment -> clientInfo.getStore() == null || clientInfo.getStore().equals(payment.getStore()))
                        .timeout(Duration.ofMillis(disconnectTimeout), Mono.empty())
                )
                .map(this::retrieveFrom)
                .reduce((paymentsStatistic1, paymentsStatistic2) -> {
                    var sumPayments = paymentsStatistic1.getTotalAmount() + paymentsStatistic2.getTotalAmount();
                    paymentsStatistic1.setTotalAmount(sumPayments);
                    paymentsStatistic1.getPayments().addAll(paymentsStatistic2.getPayments());
                    return paymentsStatistic1;
                })
                .map(paymentsStatistic -> {
                    paymentsStatistic.getPayments().sort(Comparator.comparing(Payment::getPaymentDate));
                    return paymentsStatistic;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Not found such Client")));

    }

    private PaymentsStatistic retrieveFrom(Payment payment) {
        return new PaymentsStatistic().addPaymentsItem(payment)
                .clientId(payment.getClientId())
                .clientName(payment.getClientName())
                .filtrationStore(payment.getStore())
                .totalAmount(payment.getAmountPayment());
    }
}
