package kafkagoal.paymentsprocessorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class PaymentsProcessorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentsProcessorServiceApplication.class, args);
	}

}
