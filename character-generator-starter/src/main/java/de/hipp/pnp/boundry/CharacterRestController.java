package de.hipp.pnp.boundry;

import de.hipp.pnp.rabbitmq.CharacterProducer;
import de.hipp.pnp.rabbitmq.DataProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static de.hipp.pnp.base.constants.UrlConstants.CHARACTERURL;

@Slf4j
@RestController
@RequestMapping(CHARACTERURL)
public class CharacterRestController {

    final CharacterProducer characterProducer;
    final DataProducer dataProducer;

    public CharacterRestController(CharacterProducer characterProducer, DataProducer dataProducer) {
        this.characterProducer = characterProducer;
        this.dataProducer = dataProducer;
    }

    @GetMapping
    public String getAllCharacters() {
        return characterProducer.getAllCharacters();
    }

    @GetMapping("/generate")
    public String generateCharacter(
            @RequestParam(value = "gameType", defaultValue = "1") int gameType) {
        return characterProducer.generate(gameType);
    }


}
