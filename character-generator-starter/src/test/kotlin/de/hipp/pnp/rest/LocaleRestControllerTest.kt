package de.hipp.pnp.rest

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.fivee.LanguageValue
import de.hipp.pnp.base.constants.UrlConstants
import de.hipp.pnp.rabbitmq.DataProducer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class LocaleRestControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var dataProducer: DataProducer

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @InjectMocks
    private lateinit var localeRestController: LocaleRestController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(localeRestController).build()
    }

    @Test
    fun `test getLocale returns locale information for default game type`() {
        // Given
        val gameType = 0
        val language = "en_US"
        val languageValue = LanguageValue("label1", "description1")

        // Create the nested map structure with nullable types to match the expected return type
        val innerMap: Map<String?, LanguageValue?> = mapOf("innerKey" to languageValue)
        val midMap: Map<String?, Map<String?, LanguageValue?>> = mapOf("midKey" to innerMap)
        val outerMap: Map<String?, Map<String?, Map<String?, LanguageValue?>>> = mapOf("outerKey" to midMap)
        val localeData: Map<String?, Map<String?, Map<String?, Map<String?, LanguageValue?>>>> = mapOf("key1" to outerMap)

        val jsonResponse = """{"key1":{"outerKey":{"midKey":{"innerKey":{"label":"label1","description":"description1"}}}}}"""

        `when`(dataProducer.getLanguageKeysByGameTypeAndLanguage(gameType, language)).thenReturn(localeData)
        `when`(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(localeData)).thenReturn(jsonResponse)

        // When/Then
        mockMvc.perform(get(UrlConstants.LOCALEURL))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(jsonResponse))

        // Verify
        verify(dataProducer).getLanguageKeysByGameTypeAndLanguage(gameType, language)
        verify(objectMapper.writerWithDefaultPrettyPrinter()).writeValueAsString(localeData)
    }

    @Test
    fun `test getLocale returns locale information for specific game type`() {
        // Given
        val gameType = 1
        val language = "en_US"
        val languageValue1 = LanguageValue("label1", "description1")
        val languageValue2 = LanguageValue("label2", "description2")

        // Create the nested map structure with nullable types to match the expected return type
        val innerMap1: Map<String?, LanguageValue?> = mapOf("innerKey1" to languageValue1)
        val midMap1: Map<String?, Map<String?, LanguageValue?>> = mapOf("midKey1" to innerMap1)
        val outerMap1: Map<String?, Map<String?, Map<String?, LanguageValue?>>> = mapOf("outerKey1" to midMap1)

        val innerMap2: Map<String?, LanguageValue?> = mapOf("innerKey2" to languageValue2)
        val midMap2: Map<String?, Map<String?, LanguageValue?>> = mapOf("midKey2" to innerMap2)
        val outerMap2: Map<String?, Map<String?, Map<String?, LanguageValue?>>> = mapOf("outerKey2" to midMap2)

        val localeData: Map<String?, Map<String?, Map<String?, Map<String?, LanguageValue?>>>> = mapOf(
            "key1" to outerMap1,
            "key2" to outerMap2
        )

        val jsonResponse = """{"key1":{"outerKey1":{"midKey1":{"innerKey1":{"label":"label1","description":"description1"}}}},"key2":{"outerKey2":{"midKey2":{"innerKey2":{"label":"label2","description":"description2"}}}}}"""

        `when`(dataProducer.getLanguageKeysByGameTypeAndLanguage(gameType, language)).thenReturn(localeData)
        `when`(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(localeData)).thenReturn(jsonResponse)

        // When/Then
        mockMvc.perform(get(UrlConstants.LOCALEURL).param("gameType", gameType.toString()))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(jsonResponse))

        // Verify
        verify(dataProducer).getLanguageKeysByGameTypeAndLanguage(gameType, language)
        verify(objectMapper.writerWithDefaultPrettyPrinter()).writeValueAsString(localeData)
    }
}
