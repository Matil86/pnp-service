package de.hipp.pnp.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.dto.LanguageRequest;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import de.hipp.pnp.api.fivee.LanguageValue;
import de.hipp.pnp.api.fivee.interfaces.FiveEDataProducer;
import de.hipp.pnp.base.constants.RoutingKeys;
import de.hipp.pnp.base.rabbitmq.BaseProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@Slf4j
public class DataProducer extends BaseProducer<Map<String, Map<String, Map<String, Map<String, LanguageValue>>>>> implements FiveEDataProducer {


    public DataProducer(RabbitTemplate rabbitTemplate, ObjectMapper mapper) {
        super(rabbitTemplate, mapper);
    }

    @Override
    public Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> getAllLanguageKeys() {
        Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> response = this.sendMessageForRoutingKey(RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY);
        return response == null ? Collections.EMPTY_MAP : response;
    }

    @Override
    public Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> getLanguageKeysByGameType(int gameType) {
        Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> response = this.sendMessageForRoutingKey(RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_ROUTING_KEY, E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK));
        return response == null ? Collections.EMPTY_MAP : response;
    }

    @Override
    public Map<String, Map<String, Map<String, LanguageValue>>> getLanguageKeysByGameTypeAndLanguage(int gameType, String locale) {
        LanguageRequest request = new LanguageRequest();
        request.setGameType(gameType);
        request.setLocale(locale);
        Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> responseMap = this.sendMessageForRoutingKey(RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_AND_LANGUAGE_ROUTING_KEY, E5EGameTypes.fromValue(gameType, E5EGameTypes.GENEFUNK), request);
        return responseMap.get(locale) == null ? Collections.EMPTY_MAP : responseMap.get(locale);
    }
}
