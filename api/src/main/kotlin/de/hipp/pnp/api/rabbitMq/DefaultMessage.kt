package de.hipp.pnp.api.rabbitMq

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class DefaultMessage<T> {
    lateinit var action: String
    var payload: T? = null
    lateinit var detailMessage: String
    lateinit var uuid: String
    lateinit var header: MessageHeader

    @JsonIgnore
    override fun toString(): String {
        val objectMapper = ObjectMapper()
        try {
            return objectMapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}
