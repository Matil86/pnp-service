package de.hipp.pnp.security.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import de.hipp.pnp.api.rabbitMq.DefaultMessage;
import de.hipp.pnp.api.rabbitMq.MessageHeader;
import de.hipp.pnp.base.constants.RoutingKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class UserListener {

    private final ObjectMapper mapper;
    private final UserService userService;

    public UserListener(ObjectMapper mapper, ConnectionFactory factory, UserService userService) throws IOException {
        this.mapper = mapper;
        this.userService = userService;

        declareQueues(factory.createConnection().createChannel(true));
    }

    private void declareQueues(Channel channel) throws IOException {
        channel.queueDeclare(RoutingKeys.GET_INTERNAL_USER, false, false, true, null);
    }

    @RabbitListener(queues = RoutingKeys.GET_INTERNAL_USER)
    public String handleGetInternalUserId(String user) throws JsonProcessingException {
        var message = mapper.readValue(user, new TypeReference<DefaultMessage<String>>() {
        });
        log.info("Received Get Internal User Message : {}", message);
        User customer = userService.getUserByExternalId(message.getPayload());
        log.debug("found Internal User Customer : {}", customer);
        DefaultMessage<User> response = new DefaultMessage<>();
        response.setHeader(new MessageHeader());
        response.getHeader().setExternalId(message.getPayload());
        response.getHeader().setRoles(new String[]{customer.getRole()});
        response.setPayload(customer);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
    }
}
