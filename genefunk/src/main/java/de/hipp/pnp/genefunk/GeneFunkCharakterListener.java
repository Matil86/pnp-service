package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import de.hipp.pnp.api.fivee.DefaultMessage;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class GeneFunkCharakterListener {

	public static final String GET_ALL_CHARACTERS_QUEUE = "GET_ALL_CHARACTERS";
	public static final String CREATE_CHARACTER_QUEUE = "CREATE_CHARACTER";
	private final GeneFunkCharacterService service;
	private final ObjectMapper mapper;

	public GeneFunkCharakterListener(GeneFunkCharacterService service, ObjectMapper mapper, ConnectionFactory factory) throws IOException {
		this.service = service;
		this.mapper = mapper;

		declaceQueues(factory.createConnection().createChannel(true));
	}

	private void declaceQueues(Channel channel) throws IOException {
		channel.queueDeclare(GET_ALL_CHARACTERS_QUEUE, false, false, false, null);
		channel.queueDeclare(CREATE_CHARACTER_QUEUE, false, false, false, null);
	}

	@RabbitListener(queues = GET_ALL_CHARACTERS_QUEUE)
	public String getAllGenefunkCharacters(String character) throws IOException {
		var message = mapper.readValue(character, new TypeReference<DefaultMessage<List<GeneFunkCharacter>>>() {
		});
		List<GeneFunkCharacter> genChars = service.getAllCharacters();
		message.setAction("finished");
		message.setPayload(genChars);
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
	}

	@RabbitListener(queues = CREATE_CHARACTER_QUEUE)
	protected String createGenefunkCharacter(String character) throws JsonProcessingException {
		var message = mapper.readValue(character, new TypeReference<DefaultMessage<GeneFunkCharacter>>() {
		});
		if (!message.getAction().equals(E5EGameTypes.GENEFUNK.name())) {
			return null;
		}
		GeneFunkCharacter genChar = service.generate(message.getPayload());
		message.setAction("finished");
		message.setPayload(genChar);
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
	}
}
