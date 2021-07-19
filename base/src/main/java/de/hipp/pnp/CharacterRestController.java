package de.hipp.pnp;

import de.hipp.pnp.interfaces.I5ECharacter;
import de.hipp.pnp.interfaces.I5ECharacterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.hipp.pnp.constants.UrlConstants.CHARACTERURL;

@RestController
@RequestMapping(CHARACTERURL)
public class CharacterRestController {

    final I5ECharacterService characterService;

    public CharacterRestController(I5ECharacterService service) {
        this.characterService = service;
    }

    @GetMapping
    public List getAllCharacters() {
        return characterService.getAllCharacters();
    }

    @GetMapping("/generate")
    public I5ECharacter generateCharacter() {
        return characterService.generate();
    }

}
