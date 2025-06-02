package de.hipp.pnp.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.api.fivee.interfaces.FiveECharacterProducer
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.rabbitmq.BaseProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class CharacterProducer(rabbitTemplate: RabbitTemplate?, mapper: ObjectMapper?) :
    BaseProducer<BaseCharacter?>(rabbitTemplate, mapper), FiveECharacterProducer {

    private val log = KotlinLogging.logger {}

    /**
     * Generates a character based on the provided game type.
     *
     * @param gameType The type of game for which to generate the character.
     * @return A BaseCharacter object representing the generated character, or null if generation fails.
     */
    override fun generate(gameType: Int): String {
        log.debug { "${"message to generate character for {} received"} ${E5EGameTypes.fromValue(gameType)}" }

        return mapper.writeValueAsString(
            sendMessageForRoutingKey(
                RoutingKeys.CREATE_CHARACTER_ROUTING_KEY,
                E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK)
            )
        )
    }

    override fun allCharacters(): MutableList<BaseCharacter?> {
        log.debug { "get all characters request received" }
        return sendMessageForRoutingKey(
            RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY,
            E5EGameTypes.GENEFUNK
        ) as MutableList<BaseCharacter?>
    }
}
