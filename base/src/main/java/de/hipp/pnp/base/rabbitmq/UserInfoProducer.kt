package de.hipp.pnp.base.rabbitmq

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.rabbitMq.MessageHeader
import de.hipp.pnp.base.constants.RoutingKeys.GET_INTERNAL_USER
import de.hipp.pnp.base.constants.RoutingKeys.SAVE_NEW_USER
import de.hipp.pnp.base.dto.Customer
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class UserInfoProducer(rabbitTemplate: RabbitTemplate?, mapper: ObjectMapper?) :
    BaseProducer<Map<*, *>?>(rabbitTemplate, mapper) {

    fun getCustomerInfoFor(userId: String?): Customer {
        val map = this.sendMessageForRoutingKey(GET_INTERNAL_USER, null, userId) ?: return Customer()
        return mapper.convertValue(map, Customer::class.java)
    }

    @Throws(JsonProcessingException::class)
    fun saveNewUser(customer: Customer?): Customer {
        val map = this.sendMessageForRoutingKey(SAVE_NEW_USER, null, customer)
        return mapper.convertValue(map, Customer::class.java)
    }

    override fun getHeader(): MessageHeader {
        return MessageHeader()
    }
}
