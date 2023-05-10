package de.hipp.pnp.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerConfiguration {

	static final String QUEUE_GET_ALL_CHARACTERS = "GET_ALL_GENEFUNK";

	static final String QUEUE_CREATE_GENEFUNK = "CREATE_GENEFUNK";


	@Bean
	SimpleMessageListenerContainer container(
			ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QUEUE_CREATE_GENEFUNK, QUEUE_GET_ALL_CHARACTERS);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
}
