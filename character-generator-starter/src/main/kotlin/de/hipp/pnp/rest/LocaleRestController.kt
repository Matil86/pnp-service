package de.hipp.pnp.rest

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.base.constants.UrlConstants.LOCALEURL
import de.hipp.pnp.rabbitmq.LocaleProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.annotation.PostConstruct
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
@Tag(name = "Locale Management", description = "Operations for managing game localization")
@RestController
class LocaleRestController(val localeProducer: LocaleProducer, val mapper: ObjectMapper) {

    private val log = KotlinLogging.logger {}

    /**
     * Initializes the LocaleRestController and logs the availability of the locale endpoint.
     * This method is called after dependency injection is complete.
     */
    @PostConstruct
    fun init() {
        log.info { "Initialized LocaleRestController: $LOCALEURL should be available" }
    }

    /**
     * Retrieves localized language keys for a specific game type.
     * Currently returns English (en_US) locale data.
     *
     * @param gameType The type of game for which to retrieve locale data (default: 0)
     * @return JSON string containing the localized language keys
     * @throws JsonProcessingException if there's an error serializing the locale data
     */
    @Operation(
        summary = "Get locale data",
        description = "Retrieves localized language keys for the specified game type"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved locale data",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))]
            ),
            ApiResponse(responseCode = "500", description = "Internal server error", content = [Content()])
        ]
    )
    @GetMapping(LOCALEURL)
    @Throws(JsonProcessingException::class)
    fun getLocale(
        @Parameter(name = "gameType", description = "Game type identifier", example = "0")
        @RequestParam(
            value = "gameType",
            defaultValue = "0"
        ) gameType: Int
    ): String {
        val locale = localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US")
        return mapper.writeValueAsString(locale)
    }
}
