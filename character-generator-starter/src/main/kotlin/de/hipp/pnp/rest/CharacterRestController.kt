package de.hipp.pnp.rest

import com.fasterxml.jackson.core.JsonProcessingException
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.base.constants.UrlConstants
import de.hipp.pnp.rabbitmq.CharacterProducer
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UrlConstants.CHARACTERURL)
class CharacterRestController(val characterProducer: CharacterProducer) {

    @ResponseBody
    @GetMapping()
    fun allCharacters(): List<BaseCharacter?> = characterProducer.allCharacters()

    @GetMapping("/generate")
    @ResponseBody
    @Throws(JsonProcessingException::class)
    fun generateCharacter(
        @RequestParam(value = "gameType", defaultValue = "0") gameType: Int
    ): String {
        return characterProducer.generate(gameType)
    }

    @ResponseBody
    @DeleteMapping("/{characterId}")
    fun deleteCharacter(@PathVariable(value = "characterId") characterId: Int) {
        characterProducer.deleteCharacter(characterId)
    }
}
