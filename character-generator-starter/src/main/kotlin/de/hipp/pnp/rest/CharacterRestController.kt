package de.hipp.pnp.rest

import com.fasterxml.jackson.core.JsonProcessingException
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.base.constants.UrlConstants
import de.hipp.pnp.rabbitmq.CharacterProducer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UrlConstants.CHARACTERURL)
class CharacterRestController(val characterProducer: CharacterProducer) {

    @ResponseBody
    @GetMapping("/")
    fun allCharacters(): List<BaseCharacter?> = characterProducer.allCharacters()

    @GetMapping("/generate")
    @ResponseBody
    @Throws(JsonProcessingException::class)
    fun generateCharacter(
        @RequestParam(value = "gameType", defaultValue = "0") gameType: Int
    ): String {
        return characterProducer.generate(gameType)
    }
}
