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

/**
 * REST Controller for character management operations.
 * Provides endpoints for retrieving, generating, and deleting characters.
 * 
 * @param characterProducer The producer service for character operations
 */
// TODO: Add Swagger annotations once springdoc-openapi dependency is resolved:
// @Tag(name = "Character Management", description = "Operations for managing RPG characters")
@RestController
@RequestMapping(UrlConstants.CHARACTERURL)
class CharacterRestController(val characterProducer: CharacterProducer) {

    /**
     * Retrieves all available characters.
     * 
     * @return List of all characters in the system
     */
    // TODO: Add Swagger annotation: @Operation(summary = "Get all characters", description = "Retrieves a list of all available characters")
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
    // TODO: Add Swagger annotations:
    // @Operation(summary = "Generate character", description = "Generates a new character for the specified game type")
    // @Parameter(name = "gameType", description = "Game type identifier", example = "0")
    @GetMapping("/generate")
    @ResponseBody
    @Throws(JsonProcessingException::class)
    fun generateCharacter(
        @RequestParam(value = "gameType", defaultValue = "0") gameType: Int
    ): String {
        return characterProducer.generate(gameType)
    }

    /**
     * Deletes a character by its ID.
     * 
     * @param characterId The unique identifier of the character to delete
     */
    // TODO: Add Swagger annotations:
    // @Operation(summary = "Delete character", description = "Removes a character from the system")
    // @Parameter(name = "characterId", description = "Unique character identifier", required = true)
    @ResponseBody
    @DeleteMapping("/{characterId}")
    fun deleteCharacter(@PathVariable(value = "characterId") characterId: Int) {
        characterProducer.deleteCharacter(characterId)
    }
}
