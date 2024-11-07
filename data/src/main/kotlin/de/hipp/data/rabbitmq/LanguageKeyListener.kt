package de.hipp.data.rabbitmq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import de.hipp.data.config.LanguageKeyConfiguration
import de.hipp.pnp.api.dto.LanguageRequest
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.fivee.LanguageValue
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.base.constants.RoutingKeys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class LanguageKeyListener(
    private val mapper: ObjectMapper,
    factory: ConnectionFactory,
    private val configuration: LanguageKeyConfiguration,
) {
    private val log: Logger = LoggerFactory.getLogger(LanguageKeyListener::class.java)

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
            DefaultMessage<Map<String, Map<String, Map<String, Map<String, LanguageValue>>>>>()

        val payload =
            configuration.locale

        message.action = "finished"
        message.payload = payload
        log.info("{} finished with {}", RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY, payload)
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message)
    }

    @RabbitListener(queues = [RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_AND_LANGUAGE_ROUTING_KEY])
    @Throws(
        IOException::class
    )
    fun getAllLanguageKeysByGameTypeAndLanguage(message: String): String {
        val messageObject: DefaultMessage<LanguageRequest> = mapper
            .readValue(message, object : TypeReference<DefaultMessage<LanguageRequest>>() {})

        log.info(messageObject.toString())
        val payload = messageObject.payload
        val allLocale = configuration.locale
        val games = allLocale.getOrDefault(payload.locale, allLocale.get("enUS"))
        val gameType: E5EGameTypes? = E5EGameTypes.fromValue(payload.gameType)
        val books: Map<String, Map<String, LanguageValue>>? = games?.get(gameType.toString().lowercase())
        val response: DefaultMessage<Map<String, Map<String, LanguageValue>>> = DefaultMessage()
        response.action = "finished"
        response.payload = books
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response)
    }
}
