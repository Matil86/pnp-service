package de.hipp.data.rabbitmq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import de.hipp.data.language.LanguageKeyConfiguration;
import de.hipp.pnp.api.dto.LanguageRequest;
import de.hipp.pnp.api.fivee.DefaultMessage;
import de.hipp.pnp.api.fivee.LanguageValue;
import de.hipp.pnp.base.fivee.constants.RoutingKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class LanguageKeyListener {

    private final ObjectMapper mapper;
    private final LanguageKeyConfiguration configuration;

    public LanguageKeyListener(ObjectMapper mapper, ConnectionFactory factory, LanguageKeyConfiguration configuration) throws IOException {
        this.mapper = mapper;
        this.configuration = configuration;

        declareQueues(factory.createConnection().createChannel(true));
    }

    private void declareQueues(Channel channel) throws IOException {
        channel.queueDeclare(RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY, false, false, true, null);
        channel.queueDeclare(RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_ROUTING_KEY, false, false, true, null);
        channel.queueDeclare(RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_AND_LANGUAGE_ROUTING_KEY, false, false, true, null);
    }

    @RabbitListener(queues = RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY)
    public String getAllLanguageKeys() throws IOException {
        var message = new DefaultMessage<Map<String, Map<String, Map<String, Map<String, LanguageValue>>>>>();

        Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> payload = configuration.getLocale();

        message.setAction("finished");
        message.setPayload(payload);
        log.info("{} finished with {}", RoutingKeys.GET_ALL_LANGUAGE_KEYS_ROUTING_KEY, payload);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
    }

    @RabbitListener(queues = RoutingKeys.GET_ALL_LANGUAGE_KEYS_BY_GAME_ROUTING_KEY)
    public String getAllLanguageKeysByGameType(String message) throws IOException {
        var messageObject = mapper.readValue(message, new TypeReference<DefaultMessage<LanguageRequest>>() {
        });


        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
    }
}
