package de.hipp.pnp.base.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.entity.CharacterSpeciesEntity
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class GenefunkInfoProducer(rabbitTemplate: RabbitTemplate?, mapper: ObjectMapper?) :
    BaseProducer<List<Map<*, *>?>>(rabbitTemplate, mapper) {
    fun getAllSpecies(): List<CharacterSpeciesEntity>? {
        val species = this.sendMessageForRoutingKey(RoutingKeys.GET_GENEFUNK_SPECIES, E5EGameTypes.GENEFUNK)
        return species?.map {
            val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it)
            mapper.readValue(
                json,
                CharacterSpeciesEntity::class.java
            )
        }
    }

}