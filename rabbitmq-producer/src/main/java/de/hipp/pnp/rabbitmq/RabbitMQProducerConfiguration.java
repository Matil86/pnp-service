package de.hipp.pnp.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfiguration {

	static final String TOPIC_EXCHANGE_CHARACTER_CREATION = "characterGeneration";
	static final String QUEUE_GET_ALL_CHARACTERS = "GET_ALL_GENEFUNK";

	static final String QUEUE_CREATE_GENEFUNK = "CREATE_GENEFUNK";

	@Bean
	Queue createGeneFunkQueue() {
		return new Queue(QUEUE_CREATE_GENEFUNK, false);
	}

	@Bean
	Queue getAllGeneFunkQueue() {
		return new Queue(QUEUE_GET_ALL_CHARACTERS, false);
	}

	@Bean
	TopicExchange topicExchange() {
		return new TopicExchange(TOPIC_EXCHANGE_CHARACTER_CREATION);
	}

	@Bean
	Binding createGeneFunkCharacterBinding(Queue createGeneFunkQueue, TopicExchange exchange) {
		return BindingBuilder.bind(createGeneFunkQueue).to(exchange).with(exchange.getName());
	}

	@Bean
	Binding createGetAllCharactersBindung(Queue getAllGeneFunkQueue, TopicExchange exchange) {
		return BindingBuilder.bind(getAllGeneFunkQueue).to(exchange).with(exchange.getName());
	}
}
