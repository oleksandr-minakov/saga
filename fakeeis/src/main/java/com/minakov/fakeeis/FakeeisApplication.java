package com.minakov.fakeeis;

import com.minakov.fakeeis.service.FakeeisCommandHandler;
import io.eventuate.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.commands.common.ChannelMapping;
import io.eventuate.tram.commands.common.DefaultChannelMapping;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.sagas.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaParticipantConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import({TramEventsPublisherConfiguration.class,
		TramCommandProducerConfiguration.class,
		SagaOrchestratorConfiguration.class,
		TramJdbcKafkaConfiguration.class,
		SagaParticipantConfiguration.class})
@SpringBootApplication
public class FakeeisApplication {

	public static void main(String[] args) {
		SpringApplication.run(FakeeisApplication.class, args);
	}

	@Bean
	public CommandDispatcher fakeeisCommandDispatcher(FakeeisCommandHandler target) {
		return new SagaCommandDispatcher("fakeeisCommandDispatcher", target.commandHandlers());
	}

	@Bean
	public ChannelMapping channelMapping() {
		return DefaultChannelMapping.builder().build();
	}
}
