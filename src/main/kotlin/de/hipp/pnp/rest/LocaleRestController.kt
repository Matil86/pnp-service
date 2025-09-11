package de.hipp.pnp.rest

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.base.constants.UrlConstants.LOCALEURL
import de.hipp.pnp.rabbitmq.LocaleProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller for locale and language key management.
 * Provides endpoints for retrieving localized content based on game type and language.
 *
 * @param localeProducer The producer service for locale operations
 * @param mapper Object mapper for JSON serialization
 */
// TODO: Add Swagger annotations once springdoc-openapi dependency is resolved:
// @Tag(name = "Locale Management", description = "Operations for managing game localization")
@RestController
class LocaleRestController(val localeProducer: LocaleProducer, val mapper: ObjectMapper) {

    private val log = KotlinLogging.logger {}

    /**
     * Retrieves localized language keys for a specific game type.
     * Currently hardcoded to return English (en_US) locale data.
     *
     * @param gameType The type of game for which to retrieve locale data (default: 0)
     * @return JSON string containing the localized language keys
     * @throws JsonProcessingException if there's an error serializing the locale data
     */
    // TODO: Add Swagger annotations:
    // @Operation(summary = "Get locale data", description = "Retrieves localized language keys for the specified game type")
    // @Parameter(name = "gameType", description = "Game type identifier", example = "0")
    @GetMapping(LOCALEURL)
    @Throws(JsonProcessingException::class)
    fun getLocale(
        @RequestParam(
            value = "gameType",
            defaultValue = "0"
        ) gameType: Int
    ): String {
        val locale = localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US")
        return mapper.writeValueAsString(locale)
    }
}
