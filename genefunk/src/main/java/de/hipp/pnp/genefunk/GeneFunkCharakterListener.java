package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.fivee.DefaultMessage;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GeneFunkCharakterListener {

    private final GeneFunkCharacterService service;
    private ObjectMapper mapper = new ObjectMapper();

    public GeneFunkCharakterListener(GeneFunkCharacterService service) {
        this.service = service;
    }

    @KafkaListener(topics = "GENEFUNK_generate")
    @SendTo("generate_finished")
    public String listen(String character) throws IOException {
        var message = mapper.readValue(character, new TypeReference<DefaultMessage<GeneFunkCharacter>>() {
        });
        GeneFunkCharacter genChar = service.generate(message.getPayload());
        message.setAction("finished");
        message.setPayload(genChar);
        return mapper.writeValueAsString(message);
    }

    @KafkaListener(topics = "GENEFUNK_getAll")
    @SendTo("getAll_finished")
    public String getAllCharacters(String character) throws IOException {
        var message = mapper.readValue(character, new TypeReference<DefaultMessage<List<GeneFunkCharacter>>>() {
        });
        List<GeneFunkCharacter> genChars = service.getAllCharacters();
        message.setAction("finished");
        message.setPayload(genChars);
        return mapper.writeValueAsString(message);
    }
}
