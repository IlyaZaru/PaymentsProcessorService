package kafkagoal.paymentsprocessorservice.service;

import kafka_goal.api_payments_processor_service.model.ClientInfo;
import kafka_goal.api_payments_processor_service.model.PaymentsStatistic;
import reactor.core.publisher.Mono;

public interface StatisticsComputer {
    Mono<PaymentsStatistic> compute(Mono<ClientInfo> clientInfoMono);
}
