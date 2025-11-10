package de.hipp.pnp.base.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
abstract class BaseDto {
    @JsonIgnore
    private val objectMapper = ObjectMapper()

    @JsonIgnore
    override fun toString(): String =
        try {
            objectMapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            super.toString()
        }
}
