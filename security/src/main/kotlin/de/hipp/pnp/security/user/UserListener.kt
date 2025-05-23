package de.hipp.pnp.security.user

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.api.rabbitMq.MessageHeader
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.dto.Customer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class UserListener(private val mapper: ObjectMapper, factory: ConnectionFactory, private val userService: UserService) {

    var log: Logger = LoggerFactory.getLogger(UserListener::class.java)

    init {
        declareQueues(factory.createConnection().createChannel(true))
    }

    @Throws(IOException::class)
    private fun declareQueues(channel: Channel) {
        channel.queueDeclare(RoutingKeys.GET_INTERNAL_USER, false, false, true, null)
        channel.queueDeclare(RoutingKeys.SAVE_NEW_USER, false, false, true, null)
    }

    @RabbitListener(queues = [RoutingKeys.GET_INTERNAL_USER])
    @Throws(
        JsonProcessingException::class
    )
    fun handleGetInternalUserId(user: String?): String {
        val message: DefaultMessage<String> =
            mapper.readValue(user, object : TypeReference<DefaultMessage<String>>() {})
        log.info("Received Get Internal User Message : {}", message)
        val customer: User? = userService.getUserByExternalId(message.payload)
        log.debug("found Internal User Customer : {}", customer)
        val response = DefaultMessage<User>()
        response.header = MessageHeader()
        response.header.externalId = message.payload
        response.header.roles = arrayOf(customer?.role)
        response.setPayload(customer)
        log.debug("found Internal User Response : {}", response)
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response)
    }

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
        log.info("Received Save new User Message : {}", message)
        val customer = message.payload

        val userToSafe: User = User(
            customer.userId,
            customer.vorname,
            customer.nachname,
            customer.name,
            customer.externalIdentifer,
            customer.mail,
            customer.role
        )
        var responseUser: User? = userService.saveUser(userToSafe)
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseUser)
    }
}
