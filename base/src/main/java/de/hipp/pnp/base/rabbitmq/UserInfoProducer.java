package de.hipp.pnp.base.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hipp.pnp.api.rabbitMq.MessageHeader;
import de.hipp.pnp.base.constants.RoutingKeys;
import de.hipp.pnp.base.dto.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class UserInfoProducer extends BaseProducer<Map> {

    public UserInfoProducer(RabbitTemplate rabbitTemplate, ObjectMapper mapper) {
        super(rabbitTemplate, mapper);
    }

    public Customer getCustomerInfoFor(String userId) {
        Map map = this.sendMessageForRoutingKey(RoutingKeys.GET_INTERNAL_USER, null, userId);
        return mapper.convertValue(map, Customer.class);
    }

    public Customer saveNewUser(Customer customer) throws JsonProcessingException {
        Map map = this.sendMessageForRoutingKey(RoutingKeys.SAVE_NEW_USER, null, customer);
        return mapper.convertValue(map, Customer.class);
    }

    @Override
    protected MessageHeader getHeader() {
        return new MessageHeader();
    }
}
