package de.hipp.pnp.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter;
import de.hipp.pnp.base.fivee.constants.RoutingKeys;
import de.hipp.pnp.api.fivee.interfaces.FiveECharacterProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CharacterProducer extends BaseProducer<BaseCharacter> implements FiveECharacterProducer {


    public CharacterProducer(RabbitTemplate rabbitTemplate, ObjectMapper mapper) {
        super(rabbitTemplate, mapper);
    }

    @Override
    public String generate(int gameType) {
        log.debug("message to produce received");

        return String.valueOf(sendMessageForRoutingKey(RoutingKeys.CREATE_CHARACTER_ROUTING_KEY, E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK)));
    }

    @Override
    public String getAllCharacters() {

        log.debug("message to produce received");
        return String.valueOf(sendMessageForRoutingKey(RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY));
    }
}
