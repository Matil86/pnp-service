package de.hipp.pnp;

import static de.hipp.pnp.base.fivee.constants.UrlConstants.CHARACTERURL;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.hipp.kafka.producer.CharacterServiceProducer5E;
import de.hipp.pnp.api.fivee.interfaces.I5ECharacter;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CHARACTERURL)
public class CharacterRestController {

    final CharacterServiceProducer5E characterService;

    public CharacterRestController(CharacterServiceProducer5E characterService) {
        this.characterService = characterService;
    }

    @GetMapping
    public List<Object> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    @GetMapping("/generate")
    public I5ECharacter generateCharacter(@RequestParam(value = "gameType", defaultValue = "1") int gameType )
        throws JsonProcessingException {
        return characterService.generate(gameType);
    }

}
