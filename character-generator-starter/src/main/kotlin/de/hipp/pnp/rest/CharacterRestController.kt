package de.hipp.pnp.rest

import com.fasterxml.jackson.core.JsonProcessingException
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.base.constants.UrlConstants
import de.hipp.pnp.rabbitmq.CharacterProducer
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller for character management operations.
 * Provides endpoints for retrieving, generating, and deleting characters.
 *
 * @param characterProducer The producer service for character operations
 */
@Tag(name = "Character Management", description = "Operations for managing RPG characters")
@RestController
@RequestMapping(UrlConstants.CHARACTERURL)
class CharacterRestController(val characterProducer: CharacterProducer) {

    /**
     * Retrieves all available characters.
     *
     * @return List of all characters in the system
     */
    @Operation(
        summary = "Get all characters",
        description = "Retrieves a list of all available characters"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved list of characters",
                content = [Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = BaseCharacter::class))
                )]
            ),
            ApiResponse(responseCode = "500", description = "Internal server error", content = [Content()])
        ]
    )
    @ResponseBody
    @GetMapping()
    fun allCharacters(): List<BaseCharacter?> = characterProducer.allCharacters()

    /**
     * Generates a new character based on the specified game type.
     *
     * @param gameType The type of game for character generation (default: 0)
     * @return JSON string representation of the generated character
     * @throws JsonProcessingException if there's an error processing the character data
     */
    @Operation(
        summary = "Generate character",
        description = "Generates a new character for the specified game type"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully generated character",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))]
            ),
            ApiResponse(responseCode = "500", description = "Internal server error", content = [Content()])
        ]
    )
    @GetMapping("/generate")
    @ResponseBody
    @Throws(JsonProcessingException::class)
    fun generateCharacter(
        @Parameter(name = "gameType", description = "Game type identifier", example = "0")
        @RequestParam(value = "gameType", defaultValue = "0") gameType: Int
    ): String {
        return characterProducer.generate(gameType)
    }

    /**
     * Deletes a character by its ID.
     *
     * @param characterId The unique identifier of the character to delete
     */
    @Operation(
        summary = "Delete character",
        description = "Removes a character from the system"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Successfully deleted character", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Character not found", content = [Content()]),
            ApiResponse(responseCode = "500", description = "Internal server error", content = [Content()])
        ]
    )
    @ResponseBody
    @DeleteMapping("/{characterId}")
    fun deleteCharacter(
        @Parameter(name = "characterId", description = "Unique character identifier", required = true)
        @PathVariable(value = "characterId") characterId: Int
    ) {
        characterProducer.deleteCharacter(characterId)
    }
}
