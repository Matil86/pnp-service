package de.hipp.pnp.data.rabbitmq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.pnp.api.dto.LanguageRequest
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.locale.SystemLocale
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.data.config.LocalizationProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class LanguageKeyListener(
    private val mapper: ObjectMapper,
    factory: ConnectionFactory,
    private val localizationProperties: LocalizationProperties,
) {
    private val log = KotlinLogging.logger {}

    init {
        declareQueues(factory.createConnection().createChannel(true))
    }

    @Throws(IOException::class)
    private fun declareQueues(channel: Channel) {
        channel.queueDeclare(RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY, false, false, true, null)
        channel.queueDeclare(
            RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_AND_LANGUAGE_ROUTING_KEY,
            false,
            false,
            true,
            null
        )
    }

    @RabbitListener(queues = [RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY])
    fun getAllLanguageKeys(): String {
        val message =
            DefaultMessage<List<SystemLocale>>()

        val payload =
            localizationProperties.systems.values.toMutableList()

        message.action = "finished"
        message.payload = payload
        log.info { "${RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY} finished with $payload" }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message)
    }

    @RabbitListener(queues = [RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_AND_LANGUAGE_ROUTING_KEY])
    @Throws(
        IOException::class
    )
    fun getAllLanguageKeysByGameTypeAndLanguage(message: String): String {
        val messageObject: DefaultMessage<LanguageRequest> = mapper
            .readValue(message, object : TypeReference<DefaultMessage<LanguageRequest>>() {})

        log.info { messageObject.toString() }
        val payload = messageObject.payload
        val allLocale = localizationProperties.systems

        val gameName = E5EGameTypes.fromValue(payload?.gameType).toString().lowercase()
        val game = allLocale[gameName]
            ?: throw IllegalArgumentException("Game type $gameName not found in localization properties")
        val response: DefaultMessage<SystemLocale> = DefaultMessage()
        response.action = "finished"
        response.payload = game
        return mapper.writeValueAsString(response)
    }
}
