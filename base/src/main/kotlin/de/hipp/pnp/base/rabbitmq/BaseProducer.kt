package de.hipp.pnp.base.rabbitmq

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.rabbitMq.DefaultMessage
import de.hipp.pnp.api.rabbitMq.MessageHeader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.util.UUID

@Component
abstract class BaseProducer<T>(
    protected val template: RabbitTemplate,
    protected val mapper: ObjectMapper,
) {
    protected val log: Logger = LoggerFactory.getLogger(BaseProducer::class.java)

    protected fun sendMessageForRoutingKey(routingKey: String): T? = sendMessageForRoutingKey(routingKey, null)

    protected fun sendMessageForRoutingKey(
        routingKey: String,
        e5EGameTypes: E5EGameTypes?,
    ): T? = sendMessageForRoutingKey(routingKey, e5EGameTypes, null)

    protected fun sendMessageForRoutingKey(
        routingKey: String,
        e5EGameTypes: E5EGameTypes?,
        payload: Any?,
    ): T? {
        val message =
            DefaultMessage<Any>().apply {
                header = getHeader()
                uuid = UUID.randomUUID().toString()
                if (e5EGameTypes != null) {
                    action = e5EGameTypes.name
                }
                if (payload != null) {
                    this.payload = payload
                }
            }

        prepareTemplate(routingKey)

        var responseObject: DefaultMessage<T>? = null
        try {
            val response: String? = template.convertSendAndReceive(mapper.writeValueAsString(message))?.toString()
            responseObject =
                response?.let {
                    mapper.readValue(
                        response,
                        object : TypeReference<DefaultMessage<T>>() {},
                    )
                }
        } catch (e: JsonProcessingException) {
            log.error("couldn't send message: {}", message, e)
        }

        log.debug("Response was => {}", responseObject)
        return responseObject?.payload
    }

    protected fun getHeader(): MessageHeader {
        val auth = SecurityContextHolder.getContext().authentication
        val header = MessageHeader()
        if (auth != null) {
            val user = auth.principal as Jwt
            header.externalId = user.getClaimAsString("sub")
        }
        return header
    }

    private fun prepareTemplate(exchangeName: String) {
        log.debug("preparing Template for : {}", exchangeName)
        template.routingKey = exchangeName
    }
}
