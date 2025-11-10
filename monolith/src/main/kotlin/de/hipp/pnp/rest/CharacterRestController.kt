package de.hipp.pnp.rest

import com.fasterxml.jackson.core.JsonProcessingException
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.base.constants.UrlConstants
import de.hipp.pnp.rabbitmq.CharacterProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.slf4j.MDC
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

/**
 * REST Controller for character management operations.
 * Provides endpoints for retrieving, generating, and deleting characters.
 *
 * @param characterProducer The producer service for character operations
 */
@Tag(name = "Character Management", description = "Operations for managing RPG characters")
@RestController
@RequestMapping(UrlConstants.CHARACTERURL)
@Validated
class CharacterRestController(
    val characterProducer: CharacterProducer,
    meterRegistry: MeterRegistry,
) {
    // API endpoint metrics
    private val apiCallCounter: Counter =
        Counter
            .builder("api.character.calls.total")
            .description("Total API calls to character endpoints")
            .tag("endpoint", "all")
            .register(meterRegistry)

    private val apiGenerateCounter: Counter =
        Counter
            .builder("api.character.generate.total")
            .description("Total character generation API calls")
            .register(meterRegistry)

    private val apiDeleteCounter: Counter =
        Counter
            .builder("api.character.delete.total")
            .description("Total character deletion API calls")
            .register(meterRegistry)

    private val apiListCounter: Counter =
        Counter
            .builder("api.character.list.total")
            .description("Total character list API calls")
            .register(meterRegistry)

    private val apiErrorCounter: Counter =
        Counter
            .builder("api.character.errors.total")
            .description("Total API errors")
            .register(meterRegistry)

    /**
     * Retrieves all available characters.
     *
     * @return List of all characters in the system
     */
    @Operation(
        summary = "Get all characters",
        description = "Retrieves a list of all available characters in the system",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved character list",
                content = [Content(schema = Schema(implementation = BaseCharacter::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - valid JWT token required",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()],
            ),
        ],
    )
    @ResponseBody
    @GetMapping()
    @Timed(value = "api.character.list.duration", description = "Time to list all characters")
    fun allCharacters(): List<BaseCharacter?> {
        apiCallCounter.increment()
        apiListCounter.increment()
        logger.debug { "API: Listing all characters" }

        return try {
            characterProducer.allCharacters()
        } catch (e: Exception) {
            apiErrorCounter.increment()
            logger.error(e) { "API Error: Failed to list characters" }
            throw e
        }
    }

    /**
     * Generates a new character based on the specified game type.
     *
     * @param gameType The type of game for character generation (default: 0, must be between 0-100)
     * @return JSON string representation of the generated character
     * @throws JsonProcessingException if there's an error processing the character data
     */
    @Operation(
        summary = "Generate character",
        description = "Generates a new character for the specified game type. Returns JSON representation of the created character.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Character successfully generated",
                content = [Content(schema = Schema(type = "string", example = "{\"id\":1,\"name\":\"Generated Character\"}"))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid game type parameter (must be between 0-100)",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - valid JWT token required",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error or character generation failure",
                content = [Content()],
            ),
        ],
    )
    @GetMapping("/generate")
    @ResponseBody
    @Throws(JsonProcessingException::class)
    @Timed(value = "api.character.generate.duration", description = "Time to generate character via API")
    fun generateCharacter(
        @Parameter(
            name = "gameType",
            description = "Game type identifier (0 = GeneFunk). Must be between 0-100.",
            example = "0",
            required = false,
        )
        @RequestParam(value = "gameType", defaultValue = "0")
        @Min(0, message = "Game type must be non-negative")
        @Max(100, message = "Game type must not exceed 100")
        gameType: Int,
    ): String {
        apiCallCounter.increment()
        apiGenerateCounter.increment()

        // Add game type to MDC for logging
        MDC.put("game_type", gameType.toString())

        logger.info { "API: Generating character with gameType=$gameType" }

        return try {
            val result = characterProducer.generate(gameType)
            logger.info { "API: Successfully generated character via API" }
            result
        } catch (e: Exception) {
            apiErrorCounter.increment()
            logger.error(e) { "API Error: Failed to generate character with gameType=$gameType" }
            throw e
        } finally {
            MDC.remove("game_type")
        }
    }

    /**
     * Deletes a character by its ID.
     *
     * @param characterId The unique identifier of the character to delete (must be positive)
     */
    @Operation(
        summary = "Delete character",
        description = "Removes a character from the system by its unique identifier",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Character successfully deleted",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid character ID (must be a positive integer)",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - valid JWT token required",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Character not found",
                content = [Content()],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()],
            ),
        ],
    )
    @ResponseBody
    @DeleteMapping("/{characterId}")
    @Timed(value = "api.character.delete.duration", description = "Time to delete character via API")
    fun deleteCharacter(
        @Parameter(
            name = "characterId",
            description = "Unique character identifier (must be a positive integer)",
            required = true,
            example = "123",
        )
        @PathVariable(value = "characterId")
        @Min(1, message = "Character ID must be a positive integer")
        characterId: Int,
    ) {
        apiCallCounter.increment()
        apiDeleteCounter.increment()

        // Add character ID to MDC for logging
        MDC.put("character_id", characterId.toString())

        logger.info { "API: Deleting character with id=$characterId" }

        try {
            characterProducer.deleteCharacter(characterId)
            logger.info { "API: Successfully deleted character with id=$characterId" }
        } catch (e: Exception) {
            apiErrorCounter.increment()
            logger.error(e) { "API Error: Failed to delete character with id=$characterId" }
            throw e
        } finally {
            MDC.remove("character_id")
        }
    }
}
