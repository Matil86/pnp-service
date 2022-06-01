package de.hipp.pnp.genefunk;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GeneFunkCharakterListener {

    private final GeneFunkCharacterService service;

    public GeneFunkCharakterListener(GeneFunkCharacterService service) {
        this.service = service;
    }

    @KafkaListener(id = "pnp", topics = "GENEFUNK")
    public GeneFunkCharacter listen(String value){
        return service.generate(1);
    }
}
