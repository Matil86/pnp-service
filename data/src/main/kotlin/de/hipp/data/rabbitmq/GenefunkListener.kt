package de.hipp.data.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.data.config.GameConfiguration
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.entity.CharacterSpeciesEntity
import de.hipp.pnp.base.entity.GeneFunkClass
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class GenefunkListener(
    private val mapper: ObjectMapper,
    factory: ConnectionFactory,
    private val configuration: GameConfiguration,
) {

    private val log = KotlinLogging.logger {}

    init {
        declareQueues(factory.createConnection().createChannel(true))
    }

    @Throws(IOException::class)
    private fun declareQueues(channel: Channel) {
        channel.queueDeclare(RoutingKeys.GET_GENEFUNK_CLASSES, false, false, true, null)
        channel.queueDeclare(
            RoutingKeys.GET_GENEFUNK_SPECIES,
            false,
            false,
            true,
            null
        )
    }

    @RabbitListener(queues = [RoutingKeys.GET_GENEFUNK_CLASSES])
    fun getGenefunkClasses(): String {
        val message =
            DefaultMessage<List<Map<String, GeneFunkClass>>>()

        val payload =
            configuration.books.map { it.classes }.distinct()

        message.action = "finished"
        message.payload = payload
        log.info { "${RoutingKeys.GET_GENEFUNK_CLASSES} finished with $payload" }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message)
    }

    @RabbitListener(queues = [RoutingKeys.GET_GENEFUNK_SPECIES])
    fun getGenefunkGenomes(): String {
        val message =
            DefaultMessage<List<CharacterSpeciesEntity>>()

        val payload =
            configuration.books.map { it.species }.flatten().distinct()

        message.action = "finished"
        message.payload = payload
        log.info { "${RoutingKeys.GET_GENEFUNK_SPECIES} finished with $payload" }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message)
    }
}