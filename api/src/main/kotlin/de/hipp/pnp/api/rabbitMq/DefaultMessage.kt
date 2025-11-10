package de.hipp.pnp.api.rabbitMq

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper

class DefaultMessage<T>(
    var action: String = "",
    var payload: T,
    var detailMessage: String = "",
    var uuid: String = "",
    var header: MessageHeader = MessageHeader(),
) {
    @Suppress("UNCHECKED_CAST")
    constructor() : this(
        action = "",
        payload = null as T,
        detailMessage = "",
        uuid = "",
        header = MessageHeader(),
    )

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
