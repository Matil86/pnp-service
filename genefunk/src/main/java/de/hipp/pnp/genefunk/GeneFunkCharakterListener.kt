package de.hipp.pnp.genefunk

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.base.constants.RoutingKeys
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class GeneFunkCharakterListener(
    private val service: GeneFunkCharacterService,
    private val mapper: ObjectMapper,
    factory: ConnectionFactory
) {
    private val log = KotlinLogging.logger {}

    init {
        declareQueues(factory.createConnection().createChannel(true))
    }

    @Throws(IOException::class)
    private fun declareQueues(channel: Channel) {
        channel.queueDeclare(RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY, false, false, true, null)
        channel.queueDeclare(RoutingKeys.CREATE_CHARACTER_ROUTING_KEY, false, false, true, null)
    }

    @RabbitListener(queues = [RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY])
    @Throws(IOException::class)
    fun getAllGenefunkCharacters(character: String?): String? {
        val message = mapper.readValue<DefaultMessage<MutableList<GeneFunkCharacter?>?>>(
            character,
            object : TypeReference<DefaultMessage<MutableList<GeneFunkCharacter?>?>?>() {
            })
        val payload = service.getAllCharacters(message.header.getExternalId())
        message.action = "finished"
        message.payload = payload
        log.debug { "${RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY} finished with $payload" }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message)
    }

    @RabbitListener(queues = [RoutingKeys.CREATE_CHARACTER_ROUTING_KEY])
    @Throws(JsonProcessingException::class)
    fun createGenefunkCharacter(character: String?): String? {
        val message = mapper.readValue<DefaultMessage<GeneFunkCharacter?>>(
            character,
            object : TypeReference<DefaultMessage<GeneFunkCharacter?>?>() {
            })
        if (message.action != E5EGameTypes.GENEFUNK.name) {
            return null
        }
        val payload = service.generate(message.getPayload(), message.header.getExternalId())
        message.action = "finished"
        message.payload = payload
        log.debug { "${RoutingKeys.CREATE_CHARACTER_ROUTING_KEY} finished with  $payload" }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message)
    }
}
