package de.hipp.pnp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.fivee.DefaultMessage;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RabbitMQCharacterProducer implements de.hipp.pnp.api.fivee.interfaces.FiveECharacterProducer {

	private final RabbitTemplate template;
	private final ObjectMapper mapper;

	public RabbitMQCharacterProducer(RabbitTemplate rabbitTemplate, ObjectMapper mapper) {
		this.template = rabbitTemplate;
		this.mapper = mapper;
	}

	@Override
	public String generate(int gameType) {
		log.debug("message to produce received");
		var routingKey = "CREATE_CHARACTER";

		return sendMessageForRoutingKey(routingKey, E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK));
	}

	@Override
	public String getAllCharacters() {

		log.debug("message to produce received");
		var routingKey = "GET_ALL_CHARACTERS";
		return sendMessageForRoutingKey(routingKey);
	}

	private String sendMessageForRoutingKey(String routingKey) {
		return sendMessageForRoutingKey(routingKey, null);
	}

	private String sendMessageForRoutingKey(String routingKey, E5EGameTypes e5EGameTypes) {
		prepareTemplate(routingKey);
		DefaultMessage<BaseCharacter> message = new DefaultMessage<>();
		message.setUuid(UUID.randomUUID().toString());
		if (e5EGameTypes != null) {
			message.setAction(e5EGameTypes.name());
		}

		DefaultMessage<?> responseObject = null;
		try {
			Object response = template.convertSendAndReceive(mapper.writeValueAsString(message));
			responseObject = mapper.readValue(String.valueOf(response), DefaultMessage.class);
		} catch (JsonProcessingException e) {
			log.error("couldn't send message: {}", message, e);
		}
		log.debug("Response was => {}", responseObject);
		return responseObject == null ? null : String.valueOf(responseObject.getPayload());
	}

	private void prepareTemplate(String exchangeName) {
		log.debug("preparing Template for : {}", exchangeName);
		template.setRoutingKey(exchangeName);
	}
}
