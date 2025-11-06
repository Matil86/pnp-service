package de.hipp.pnp.api.fivee.abstracts

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper

open class BaseCharacter {
    var gameType: Int = 0
    open var firstName: String? = null
    open var lastName: String? = null
    open var level: Int? = null
    var characterClasses: MutableSet<BaseCharacterClass?>? = null

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
