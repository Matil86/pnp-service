package de.hipp.pnp.security.user

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.rabbitMq.MessageHeader
import de.hipp.pnp.base.constants.RoutingKeys
import de.hipp.pnp.base.dto.Customer
import de.hipp.pnp.base.rabbitmq.BaseProducer
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class UserInfoProducer(rabbitTemplate: RabbitTemplate, mapper: ObjectMapper) :
    BaseProducer<Map<*, *>?>(rabbitTemplate, mapper) {

    fun getCustomerInfoFor(userId: String?): Customer {
        val map = this.sendMessageForRoutingKey(RoutingKeys.GET_INTERNAL_USER, null, userId) ?: return Customer()
        return mapper.convertValue(map, Customer::class.java)
    }

    @Throws(JsonProcessingException::class)
    fun saveNewUser(customer: Customer?): Customer {
        val map = this.sendMessageForRoutingKey(RoutingKeys.SAVE_NEW_USER, null, customer)
        if (map == null) {
            log.warn("Received null response from saveNewUser for customer: {}", customer)
            return Customer()
        }
        return mapper.convertValue(map, Customer::class.java)
    }

    override fun getHeader(): MessageHeader {
        return MessageHeader()
    }
}