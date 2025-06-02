package de.hipp.pnp.rest

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.base.constants.UrlConstants.LOCALEURL
import de.hipp.pnp.rabbitmq.LocaleProducer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LocaleRestController(val localeProducer: LocaleProducer, val mapper: ObjectMapper) {
    @GetMapping(LOCALEURL)
    @Throws(JsonProcessingException::class)
    fun getLocale(
        @RequestParam(
            value = "gameType",
            defaultValue = "0"
        ) gameType: Int
    ): String {
        val locale = localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US")
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(locale)
    }
}
