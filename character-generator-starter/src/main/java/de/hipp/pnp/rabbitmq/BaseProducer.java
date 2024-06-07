package de.hipp.pnp.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.fivee.DefaultMessage;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class BaseProducer<T> {

    protected final RabbitTemplate template;
    protected final ObjectMapper mapper;

    public BaseProducer(RabbitTemplate rabbitTemplate, ObjectMapper mapper) {
        this.template = rabbitTemplate;
        this.mapper = mapper;
    }

    protected T sendMessageForRoutingKey(String routingKey) {
        return sendMessageForRoutingKey(routingKey, null);
    }

    protected T sendMessageForRoutingKey(String routingKey, E5EGameTypes e5EGameTypes) {
        return sendMessageForRoutingKey(routingKey, e5EGameTypes, null);
    }

    protected T sendMessageForRoutingKey(String routingKey, E5EGameTypes e5EGameTypes, Object payload) {
        prepareTemplate(routingKey);
        DefaultMessage<Object> message = new DefaultMessage<>();
        message.setUuid(UUID.randomUUID().toString());
        if (e5EGameTypes != null) {
            message.setAction(e5EGameTypes.name());
        }
        if (payload != null) {
            message.setPayload(payload);
        }

        DefaultMessage<T> responseObject = null;
        try {
            Object response = template.convertSendAndReceive(mapper.writeValueAsString(message));
            responseObject = mapper.readValue(String.valueOf(response), new TypeReference<DefaultMessage<T>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("couldn't send message: {}", message, e);
        }
        log.debug("Response was => {}", responseObject);
        return responseObject == null ? null : responseObject.getPayload();
    }

    private void prepareTemplate(String exchangeName) {
        log.debug("preparing Template for : {}", exchangeName);
        template.setRoutingKey(exchangeName);
    }
}
