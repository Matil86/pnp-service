package de.hipp.pnp.base.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import de.hipp.pnp.api.rabbitMq.DefaultMessage;
import de.hipp.pnp.api.rabbitMq.MessageHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public abstract class BaseProducer<T> {

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
        DefaultMessage<Object> message = new DefaultMessage<>();
        message.setHeader(getHeader());
        message.setUuid(UUID.randomUUID().toString());
        prepareTemplate(routingKey);
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

    protected MessageHeader getHeader() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        DefaultOidcUser user = (DefaultOidcUser) auth.getPrincipal();
        MessageHeader header = new MessageHeader();
        header.setExternalId(user.getName());
        return header;
    }

    ;

    private void prepareTemplate(String exchangeName) {
        log.debug("preparing Template for : {}", exchangeName);
        template.setRoutingKey(exchangeName);
    }
}
