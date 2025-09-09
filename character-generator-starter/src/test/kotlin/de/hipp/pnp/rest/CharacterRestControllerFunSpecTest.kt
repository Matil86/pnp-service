package de.hipp.pnp.rest

import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.base.constants.UrlConstants
import de.hipp.pnp.rabbitmq.CharacterProducer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * FunSpec tests for CharacterRestController.
 * Tests all endpoints for proper functionality and error handling.
 * Replaces the original JUnit-based CharacterRestControllerTest.
 */
class CharacterRestControllerFunSpecTest : FunSpec({

    lateinit var mockMvc: MockMvc
    lateinit var characterProducer: CharacterProducer
    lateinit var characterRestController: CharacterRestController

    beforeTest {
        characterProducer = mockk()
        characterRestController = CharacterRestController(characterProducer)
        mockMvc = MockMvcBuilders.standaloneSetup(characterRestController).build()
    }

    context("getAllCharacters endpoint") {
        
        test("should return all characters successfully") {
            // Given
            val mockCharacters = listOf<BaseCharacter?>(mockk(), mockk())
            every { characterProducer.allCharacters() } returns mockCharacters

            // When & Then
            mockMvc.perform(get(UrlConstants.CHARACTERURL))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

            verify { characterProducer.allCharacters() }
        }

        test("should handle empty character list") {
            // Given
            val emptyCharacters = emptyList<BaseCharacter?>()
            every { characterProducer.allCharacters() } returns emptyCharacters

            // When & Then
            mockMvc.perform(get(UrlConstants.CHARACTERURL))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

            verify { characterProducer.allCharacters() }
        }
    }

    context("generateCharacter endpoint") {
        
        test("should generate character with default game type") {
            // Given
            val defaultGameType = 0
            val generatedCharacterJson = """{"name":"GeneratedCharacter","gameType":0}"""
            every { characterProducer.generate(defaultGameType) } returns generatedCharacterJson

            // When & Then
            val result = mockMvc.perform(get("${UrlConstants.CHARACTERURL}/generate"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

            result.response.contentAsString shouldContain "GeneratedCharacter"
            verify { characterProducer.generate(defaultGameType) }
        }

        test("should generate character with specific game type") {
            // Given
            val gameType = 1
            val generatedCharacterJson = """{"name":"SpecificCharacter","gameType":1}"""
            every { characterProducer.generate(gameType) } returns generatedCharacterJson

            // When & Then
            val result = mockMvc.perform(
                get("${UrlConstants.CHARACTERURL}/generate")
                    .param("gameType", gameType.toString())
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

            result.response.contentAsString shouldContain "SpecificCharacter"
            result.response.contentAsString shouldContain "\"gameType\":1"
            verify { characterProducer.generate(gameType) }
        }

        test("should handle invalid game type parameter") {
            // When & Then
            mockMvc.perform(
                get("${UrlConstants.CHARACTERURL}/generate")
                    .param("gameType", "invalid")
            )
                .andExpect(status().isBadRequest)
        }
    }

    context("deleteCharacter endpoint") {
        
        test("should delete character successfully") {
            // Given
            val characterId = 123
            every { characterProducer.deleteCharacter(characterId) } returns Unit

            // When & Then
            mockMvc.perform(delete("${UrlConstants.CHARACTERURL}/$characterId"))
                .andExpect(status().isOk)

            verify { characterProducer.deleteCharacter(characterId) }
        }

        test("should handle non-existent character deletion") {
            // Given
            val nonExistentId = 999
            every { characterProducer.deleteCharacter(nonExistentId) } returns Unit

            // When & Then
            mockMvc.perform(delete("${UrlConstants.CHARACTERURL}/$nonExistentId"))
                .andExpect(status().isOk)

            verify { characterProducer.deleteCharacter(nonExistentId) }
        }

        test("should handle invalid character ID") {
            // When & Then
            mockMvc.perform(delete("${UrlConstants.CHARACTERURL}/invalid"))
                .andExpect(status().isBadRequest)
        }
    }
})