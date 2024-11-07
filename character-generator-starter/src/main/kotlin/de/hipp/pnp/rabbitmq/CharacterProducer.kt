package de.hipp.pnp.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.api.fivee.interfaces.FiveECharacterProducer
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.rabbitmq.BaseProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class CharacterProducer(rabbitTemplate: RabbitTemplate?, mapper: ObjectMapper?) :
    BaseProducer<BaseCharacter?>(rabbitTemplate, mapper), FiveECharacterProducer {

    var log: Logger = LoggerFactory.getLogger(javaClass)
    override fun generate(gameType: Int): String {
        log.debug("message to produce received")

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            sendMessageForRoutingKey(
                RoutingKeys.CREATE_CHARACTER_ROUTING_KEY,
                E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK)
            )
        )
    }

    override fun getAllCharacters(): String {
        log.debug("message to produce received")
        return sendMessageForRoutingKey(RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY).toString()
    }
}
