package de.hipp.pnp.security.user

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.api.rabbitMq.MessageHeader
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.security.Role
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * RabbitMQ message listener for user-related operations.
 * Handles incoming messages for user retrieval and creation via message queues.
 *
 * @param mapper JSON object mapper for message serialization/deserialization
 * @param factory RabbitMQ connection factory for queue management
 * @param userService Service for user operations
 */
@Component
class UserListener(private val mapper: ObjectMapper, factory: ConnectionFactory, private val userService: UserService) {

    private val log = KotlinLogging.logger {}

    init {
        declareQueues(factory.createConnection().createChannel(true))
    }

    /**
     * Declares the necessary RabbitMQ queues for user operations.
     *
     * @param channel The RabbitMQ channel to use for queue declaration
     * @throws IOException if there's an error declaring the queues
     */
    @Throws(IOException::class)
    private fun declareQueues(channel: Channel) {
        channel.queueDeclare(RoutingKeys.GET_INTERNAL_USER, false, false, true, null)
        channel.queueDeclare(RoutingKeys.SAVE_NEW_USER, false, false, true, null)
    }

    /**
     * Handles messages for retrieving internal user information by external ID.
     * Processes incoming requests to find user data and returns user information with roles.
     *
     * @param user JSON string containing the user request message
     * @return JSON string with user information and roles, or empty user data if not found
     * @throws JsonProcessingException if there's an error processing the JSON message
     */
    @RabbitListener(queues = [RoutingKeys.GET_INTERNAL_USER])
    @Throws(
        JsonProcessingException::class
    )
    fun handleGetInternalUserId(user: String?): String {
        val message: DefaultMessage<String> =
            mapper.readValue(user, object : TypeReference<DefaultMessage<String>>() {})
        log.debug { "Received Get Internal User Message : $message" }
        val customer: User? = userService.getUserByExternalId(message.payload)
        val response = DefaultMessage<User>()
        response.header = MessageHeader()
        response.header.externalId = message.payload
        response.header.roles = arrayOf(customer?.role ?: Role.ANONYMOUS.name)
        if (customer != null) {
            response.payload = customer
            log.debug { "found Internal User Response : $response" }
        }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response)
    }

    /**
     * Handles messages for creating or retrieving users based on customer data.
     * Creates new users if they don't exist, or returns existing user information.
     *
     * @param user JSON string containing the customer data for user creation
     * @return JSON string with the saved or existing user information
     * @throws JsonProcessingException if there's an error processing the JSON message
     */
    @RabbitListener(queues = [RoutingKeys.SAVE_NEW_USER])
    @Throws(
        JsonProcessingException::class
    )
    fun handleSaveNewUser(user: String?): String {
        val message: DefaultMessage<Customer> =
            try {
                mapper.readValue(user, object : TypeReference<DefaultMessage<Customer>>() {})
            } catch (e: Exception) {
                return e.message ?: ""
            }
        log.info { "Received Save new User Message : $message" }
        val customer = message.payload
        var user: User? = userService.getUserByExternalId(externalUserId = customer.externalIdentifer)
        if (user == null) {
            val userToSafe = User(
                customer.userId ?: "",
                customer.vorname,
                customer.nachname,
                customer.name,
                customer.externalIdentifer,
                customer.mail,
                customer.role
            )
            user = userService.saveUser(userToSafe)
        }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user)
    }
}
