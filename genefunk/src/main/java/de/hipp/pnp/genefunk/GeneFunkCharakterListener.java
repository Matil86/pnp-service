package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.fivee.DefaultMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @KafkaListener(id = "pnp", topics = "GENEFUNK_generate")
    @SendTo("GENEFUNK_generate")
    public GeneFunkCharacter listen(String character) throws IOException {
        DefaultMessage<GeneFunkCharacter> parsedChar = mapper.readValue(character.getBytes(StandardCharsets.UTF_8),
            DefaultMessage.class);
        log.info(String.valueOf(parsedChar));
        return service.generate(1);
    }
}
