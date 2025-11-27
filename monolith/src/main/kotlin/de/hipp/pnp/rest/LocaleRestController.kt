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
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.springframework.validation.annotation.Validated
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
@Tag(name = "Locale Management", description = "Operations for managing game localization and language keys")
@RestController
@Validated
class LocaleRestController(
    val localeProducer: LocaleProducer,
    val mapper: ObjectMapper,
) {
    private val log = KotlinLogging.logger {}

    /**
     * Initializes the LocaleRestController and logs the availability of the locale endpoint.
     * This method is called after dependency injection is complete.
     */
    @PostConstruct
    fun init() {
        log.info { "----->Initialized LocaleRestController: $LOCALEURL should be available" }
    }

    /**
     * Retrieves localized language keys for a specific game type and language.
     * Currently hardcoded to return English (en_US) locale data.
     *
     * @param gameType The type of game for which to retrieve locale data (default: 0, must be between 0-100)
     * @param language The locale code in format xx_XX or xx-XX (e.g., en_US, de_DE)
     * @return JSON string containing the localized language keys
     * @throws JsonProcessingException if there's an error serializing the locale data
     */
    @Operation(
        summary = "Get locale data",
        description =
            "Retrieves localized language keys for the specified game type and language. " +
                "Returns JSON containing all localized strings for UI elements, skills, attributes, and game-specific content.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved locale data",
                content = [
                    Content(
                        schema =
                            Schema(
                                type = "string",
                                example = "{\"skills\":{\"acrobatics\":\"Acrobatics\",\"athletics\":\"Athletics\"}}",
                            ),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid parameters (game type must be 0-100, language must match format xx_XX or xx-XX)",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - valid JWT token required",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Locale data not found for specified game type or language",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error or JSON serialization failure",
                content = [Content()],
            ),
        ],
    )
    @GetMapping(LOCALEURL)
    @Throws(JsonProcessingException::class)
    fun getLocale(
        @Parameter(
            name = "gameType",
            description = "Game type identifier (0 = GeneFunk). Must be between 0-100.",
            example = "0",
            required = false,
        )
        @RequestParam(
            value = "gameType",
            defaultValue = "0",
        )
        @Min(0, message = "Game type must be non-negative")
        @Max(100, message = "Game type must not exceed 100")
        gameType: Int,
        @Parameter(
            name = "language",
            description = "Locale code in format xx_XX or xx-XX (e.g., en_US for English, de_DE for German)",
            example = "en_US",
            required = false,
        )
        @RequestParam(
            value = "language",
            defaultValue = "en_US",
        )
        @Pattern(
            regexp = "^[a-z]{2}[_-][A-Z]{2}$",
            message = "Language must be in format xx_XX or xx-XX (e.g., en_US, de_DE)",
        )
        language: String = "en_US",
    ): String {
        val locale = localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, language)
        return mapper.writeValueAsString(locale)
    }
}
