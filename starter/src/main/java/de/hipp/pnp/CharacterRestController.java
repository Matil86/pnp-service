package de.hipp.pnp;

import de.hipp.pnp.api.fivee.interfaces.FiveECharacterProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static de.hipp.pnp.base.fivee.constants.UrlConstants.CHARACTERURL;

@RestController
@RequestMapping(CHARACTERURL)
public class CharacterRestController {

	final FiveECharacterProducer characterService;

	public CharacterRestController(FiveECharacterProducer characterService) {
		this.characterService = characterService;
	}

	@GetMapping
	public String getAllCharacters() {
		return characterService.getAllCharacters();
	}

	@GetMapping("/generate")
	public String generateCharacter(
			@RequestParam(value = "gameType", defaultValue = "1") int gameType) {
		return characterService.generate(gameType);
	}

}
