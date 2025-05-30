package de.hipp.pnp.security.user

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.dto.Customer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.amqp.rabbit.connection.Connection
import org.springframework.amqp.rabbit.connection.ConnectionFactory

@ExtendWith(MockitoExtension::class)
class UserListenerTest {

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @Mock
    private lateinit var connectionFactory: ConnectionFactory

    @Mock
    private lateinit var connection: Connection

    @Mock
    private lateinit var channel: Channel

    @Mock
    private lateinit var userService: UserService

    private lateinit var userListener: UserListener

    @BeforeEach
    fun setUp() {
        `when`(connectionFactory.createConnection()).thenReturn(connection)
        `when`(connection.createChannel(true)).thenReturn(channel)
        userListener = UserListener(objectMapper, connectionFactory, userService)
        verify(channel).queueDeclare(RoutingKeys.GET_INTERNAL_USER, false, false, true, null)
        verify(channel).queueDeclare(RoutingKeys.SAVE_NEW_USER, false, false, true, null)
    }

    @Test
    fun `test handleGetInternalUserId returns user as JSON when user exists`() {
        // Given
        val externalId = "ext-123"
        val user = User(
            userId = "test-id",
            vorname = "John",
            nachname = "Doe",
            externalIdentifer = externalId,
            role = "USER"
        )
        val message = DefaultMessage<String>()
        message.payload = externalId

        val messageJson = """{"payload":"$externalId"}"""
        
        `when`(objectMapper.readValue(anyString(), any(TypeReference::class.java))).thenReturn(message)
        `when`(userService.getUserByExternalId(externalId)).thenReturn(user)
        `when`(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(any())).thenReturn("{\"user\":\"data\"}")

        // When
        val result = userListener.handleGetInternalUserId(messageJson)

        // Then
        assertEquals("{\"user\":\"data\"}", result)
        verify(userService).getUserByExternalId(externalId)
    }

    @Test
    fun `test handleSaveNewUser saves user and returns user as JSON`() {
        // Given
        val customer = Customer()
        customer.userId = "test-id"
        customer.vorname = "John"
        customer.nachname = "Doe"
        customer.externalIdentifer = "ext-123"
        customer.role = "USER"
        
        val message = DefaultMessage<Customer>()
        message.payload = customer

        val messageJson = """{"payload":{"userId":"test-id"}}"""
        
        val savedUser = User(
            userId = "test-id",
            vorname = "John",
            nachname = "Doe",
            externalIdentifer = "ext-123",
            role = "USER"
        )
        
        `when`(objectMapper.readValue(anyString(), any(TypeReference::class.java))).thenReturn(message)
        `when`(userService.saveUser(any())).thenReturn(savedUser)
        `when`(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(any())).thenReturn("{\"user\":\"saved\"}")

        // When
        val result = userListener.handleSaveNewUser(messageJson)

        // Then
        assertEquals("{\"user\":\"saved\"}", result)
        verify(userService).saveUser(any())
    }

    @Test
    fun `test handleSaveNewUser returns error message when JSON parsing fails`() {
        // Given
        val messageJson = """invalid json"""
        val errorMessage = "JSON parsing error"
        
        `when`(objectMapper.readValue(anyString(), any(TypeReference::class.java))).thenThrow(RuntimeException(errorMessage))

        // When
        val result = userListener.handleSaveNewUser(messageJson)

        // Then
        assertEquals(errorMessage, result)
        verify(userService, never()).saveUser(any())
    }
}