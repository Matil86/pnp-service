package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.fivee.DefaultMessage;
import de.hipp.pnp.rabbitmq.Receiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class GeneFunkCharakterListener extends Receiver {

	private final GeneFunkCharacterService service;
	private final ObjectMapper mapper;

	public GeneFunkCharakterListener(GeneFunkCharacterService service, ObjectMapper mapper) {
		super();
		this.service = service;
		this.mapper = mapper;
	}

	public String getAllCharacters(String character) throws IOException {
		var message = mapper.readValue(character, new TypeReference<DefaultMessage<List<GeneFunkCharacter>>>() {
		});
		List<GeneFunkCharacter> genChars = service.getAllCharacters();
		message.setAction("finished");
		message.setPayload(genChars);
		return mapper.writeValueAsString(message);
	}

	@Override
	protected String handleMessage(String character) throws JsonProcessingException {
		var message = mapper.readValue(character, new TypeReference<DefaultMessage<GeneFunkCharacter>>() {
		});
		GeneFunkCharacter genChar = service.generate(message.getPayload());
		message.setAction("finished");
		message.setPayload(genChar);
		return mapper.writeValueAsString(message);
	}
}
