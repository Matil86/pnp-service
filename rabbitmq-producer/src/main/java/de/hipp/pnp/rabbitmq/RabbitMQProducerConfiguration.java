package de.hipp.pnp.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:rabbitMQ.yml")
public class RabbitMQProducerConfiguration {

	@Value("${spring.rabbitmq.host}")
	String host;
	@Value("${spring.rabbitmq.username}")
	String username;
	@Value("${spring.rabbitmq.password}")
	String password;
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

	@Bean
	public CachingConnectionFactory ccf() {
		CachingConnectionFactory ccf = new CachingConnectionFactory();
		ccf.setAddresses(host + ":5672");
		ccf.setUsername(username);
		ccf.setPassword(password);
		ccf.setVirtualHost(username);
		ccf.setAddressShuffleMode(AbstractConnectionFactory.AddressShuffleMode.INORDER);
		return ccf;
	}
}
