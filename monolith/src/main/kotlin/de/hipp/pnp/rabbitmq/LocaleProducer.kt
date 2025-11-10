package de.hipp.pnp.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.dto.LanguageRequest
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.fivee.interfaces.FiveEDataProducer
import de.hipp.pnp.api.locale.BookLocale
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.rabbitmq.BaseProducer
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class LocaleProducer(
    rabbitTemplate: RabbitTemplate,
    mapper: ObjectMapper,
) : BaseProducer<MutableMap<String, BookLocale>>(rabbitTemplate, mapper),
    FiveEDataProducer {
    override fun getAllLanguageKeys(): MutableMap<String, BookLocale>? {
        val response = this.sendMessageForRoutingKey(RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY)
        return response
    }

    override fun getLanguageKeysByGameTypeAndLanguage(
        gameType: Int,
        locale: String?,
    ): MutableMap<String, BookLocale>? {
        val request = LanguageRequest()
        request.gameType = gameType
        request.locale = locale
        val responseMap =
            this.sendMessageForRoutingKey(
                RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_AND_LANGUAGE_ROUTING_KEY,
                E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK),
                request,
            )
        return responseMap
    }
}
