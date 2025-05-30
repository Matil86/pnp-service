package de.hipp.pnp.rest

import de.hipp.pnp.base.constants.UrlConstants
import de.hipp.pnp.rabbitmq.CharacterProducer
import de.hipp.pnp.rabbitmq.DataProducer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class CharacterRestControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var characterProducer: CharacterProducer

    @Mock
    private lateinit var dataProducer: DataProducer

    @InjectMocks
    private lateinit var characterRestController: CharacterRestController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(characterRestController).build()
    }

    @Test
    fun `test getAllCharacters returns all characters`() {
        // Given
        val charactersJson = """[{"name":"Character1"},{"name":"Character2"}]"""
        `when`(characterProducer.allCharacters).thenReturn(charactersJson)

        // When/Then
        mockMvc.perform(get(UrlConstants.CHARACTERURL))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(charactersJson))

        // Verify
        verify(characterProducer).allCharacters
    }

    @Test
    fun `test generateCharacter returns generated character for default game type`() {
        // Given
        val gameType = 0
        val characterJson = """{"name":"GeneratedCharacter","gameType":0}"""
        `when`(characterProducer.generate(gameType)).thenReturn(characterJson)

        // When/Then
        mockMvc.perform(get("${UrlConstants.CHARACTERURL}/generate"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(characterJson))

        // Verify
        verify(characterProducer).generate(gameType)
    }

    @Test
    fun `test generateCharacter returns generated character for specific game type`() {
        // Given
        val gameType = 1
        val characterJson = """{"name":"GeneratedCharacter","gameType":1}"""
        `when`(characterProducer.generate(gameType)).thenReturn(characterJson)

        // When/Then
        mockMvc.perform(
            get("${UrlConstants.CHARACTERURL}/generate")
                .param("gameType", gameType.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(characterJson))

        // Verify
        verify(characterProducer).generate(gameType)
    }
}