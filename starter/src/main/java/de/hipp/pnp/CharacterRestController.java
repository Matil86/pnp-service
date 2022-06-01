package de.hipp.pnp;

import de.hipp.kafka.producer.CharacterServiceProducer5E;
import de.hipp.pnp.interfaces.I5ECharacter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.hipp.pnp.api.constants.UrlConstants.CHARACTERURL;

@RestController
@RequestMapping(CHARACTERURL)
public class CharacterRestController<T extends I5ECharacter> {

    final CharacterServiceProducer5E characterService;

    public CharacterRestController(CharacterServiceProducer5E characterService) {
        this.characterService = characterService;
    }

    @GetMapping
    public List<Object> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    @GetMapping("/generate")
    public I5ECharacter generateCharacter(@RequestParam(value = "gameType", defaultValue = "1") int gameType ) {
        return characterService.generate(gameType);
    }

}
