package de.hipp.pnp.rest

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.base.constants.UrlConstants.LOCALEURL
import de.hipp.pnp.rabbitmq.LocaleProducer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * FunSpec tests for LocaleRestController.
 * Tests all endpoints for proper functionality and error handling.
 */
class LocaleRestControllerTest : FunSpec({

    lateinit var mockMvc: MockMvc
    lateinit var localeProducer: LocaleProducer
    lateinit var objectMapper: ObjectMapper
    lateinit var localeRestController: LocaleRestController

    beforeTest {
        localeProducer = mockk()
        objectMapper = ObjectMapper()
        localeRestController = LocaleRestController(localeProducer, objectMapper)
        mockMvc = MockMvcBuilders.standaloneSetup(localeRestController).build()
    }

    context("getLocale endpoint") {
        
        test("should return locale data for default game type") {
            // Given
            val gameType = 0
            val expectedLocale = mapOf("key1" to "value1", "key2" to "value2")
            every { 
                localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US") 
            } returns expectedLocale

            // When & Then
            val result = mockMvc.perform(get(LOCALEURL))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

            result.response.contentAsString shouldContain "key1"
            result.response.contentAsString shouldContain "value1"
            
            verify { localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US") }
        }

        test("should return locale data for specific game type") {
            // Given
            val gameType = 1
            val expectedLocale = mapOf("combat" to "Combat", "magic" to "Magic")
            every { 
                localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US") 
            } returns expectedLocale

            // When & Then
            val result = mockMvc.perform(
                get(LOCALEURL)
                    .param("gameType", gameType.toString())
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

            result.response.contentAsString shouldContain "combat"
            result.response.contentAsString shouldContain "Combat"
            
            verify { localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US") }
        }

        test("should handle empty locale data") {
            // Given
            val gameType = 99
            val emptyLocale = emptyMap<String, String>()
            every { 
                localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US") 
            } returns emptyLocale

            // When & Then
            val result = mockMvc.perform(
                get(LOCALEURL)
                    .param("gameType", gameType.toString())
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()

            result.response.contentAsString shouldBe "{}"
            
            verify { localeProducer.getLanguageKeysByGameTypeAndLanguage(gameType, "en_US") }
        }

        test("should handle invalid game type parameter") {
            // Given
            val defaultGameType = 0
            val expectedLocale = mapOf("default" to "Default")
            every { 
                localeProducer.getLanguageKeysByGameTypeAndLanguage(defaultGameType, "en_US") 
            } returns expectedLocale

            // When & Then
            mockMvc.perform(
                get(LOCALEURL)
                    .param("gameType", "invalid")
            )
                .andExpect(status().isBadRequest)
        }
    }
})