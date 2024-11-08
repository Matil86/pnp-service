package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import de.hipp.pnp.api.rabbitMq.DefaultMessage;
import de.hipp.pnp.base.constants.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class GeneFunkCharakterListener {

    private Logger log = LoggerFactory.getLogger(GeneFunkCharakterListener.class);

    private final GeneFunkCharacterService service;
    private final ObjectMapper mapper;

    public GeneFunkCharakterListener(GeneFunkCharacterService service, ObjectMapper mapper, ConnectionFactory factory) throws IOException {
        this.service = service;
        this.mapper = mapper;

        declareQueues(factory.createConnection().createChannel(true));
    }

    private void declareQueues(Channel channel) throws IOException {
        channel.queueDeclare(RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY, false, false, true, null);
        channel.queueDeclare(RoutingKeys.CREATE_CHARACTER_ROUTING_KEY, false, false, true, null);
    }

    @RabbitListener(queues = RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY)
    public String getAllGenefunkCharacters(String character) throws IOException {
        var message = mapper.readValue(character, new TypeReference<DefaultMessage<List<GeneFunkCharacter>>>() {
        });
        List<GeneFunkCharacter> payload = service.getAllCharacters(message.getHeader().getExternalId());
        message.setAction("finished");
        message.setPayload(payload);
        log.info("{} finished with {}", RoutingKeys.GET_ALL_CHARACTERS_ROUTING_KEY, payload);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
    }

    @RabbitListener(queues = RoutingKeys.CREATE_CHARACTER_ROUTING_KEY)
    protected String createGenefunkCharacter(String character) throws JsonProcessingException {
        var message = mapper.readValue(character, new TypeReference<DefaultMessage<GeneFunkCharacter>>() {
        });
        if (!message.getAction().equals(E5EGameTypes.GENEFUNK.name())) {
            return null;
        }
        GeneFunkCharacter payload = service.generate(message.getPayload(), message.getHeader().getExternalId());
        message.setAction("finished");
        message.setPayload(payload);
        log.info("{} finished with {}", RoutingKeys.CREATE_CHARACTER_ROUTING_KEY, payload);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
    }
}
