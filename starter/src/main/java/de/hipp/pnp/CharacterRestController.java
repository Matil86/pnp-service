package de.hipp.pnp;

import de.hipp.pnp.interfaces.I5ECharacter;
import de.hipp.pnp.interfaces.I5ECharacterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.hipp.pnp.api.constants.UrlConstants.CHARACTERURL;

@RestController
@RequestMapping(CHARACTERURL)
public class CharacterRestController<T extends I5ECharacter> {

    final I5ECharacterService<T> characterService;

    public CharacterRestController(I5ECharacterService<T> characterService) {
        this.characterService = characterService;
    }

    @GetMapping
    public List<T> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    @GetMapping("/generate")
    public I5ECharacter generateCharacter() {
        return characterService.generate();
    }

}
