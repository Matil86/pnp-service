package de.hipp.pnp.boundry

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.base.constants.UrlConstants.LOCALEURL
import de.hipp.pnp.rabbitmq.DataProducer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LocaleRestController(val dataProducer: DataProducer, val mapper: ObjectMapper) {
    @GetMapping(LOCALEURL)
    @Throws(JsonProcessingException::class)
    fun getLocale(@RequestParam(value = "gameType", defaultValue = "0") gameType: Int): String {
        val locale = dataProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US")
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(locale)
    }
}
