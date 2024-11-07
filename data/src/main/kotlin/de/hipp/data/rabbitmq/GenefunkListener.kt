package de.hipp.data.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.data.config.GameConfiguration
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.base.constants.RoutingKeys
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
            RoutingKeys.GET_GENEFUNK_GENOMES,
            false,
            false,
            true,
            null
        )
    }

    @RabbitListener(queues = [RoutingKeys.GET_GENEFUNK_CLASSES])
    fun getAllLanguageKeys(): String {
        val message =
            DefaultMessage<List<String>>()

        val payload =
            configuration.books.map { it.species }.flatten().map { it.name }.distinct()

        message.action = "finished"
        message.payload = payload
        log.info { "${RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY} finished with $payload" }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message)
    }
}