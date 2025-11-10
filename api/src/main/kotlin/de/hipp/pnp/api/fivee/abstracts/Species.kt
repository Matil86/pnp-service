package de.hipp.pnp.api.fivee.abstracts

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class Species {
    open var name: String? = null
    open var description: String? = null
    var attributes: MutableMap<String?, Int?> = HashMap<String?, Int?>()

    @JsonIgnore
    fun addAttributeChange(
        key: String?,
        value: Int?,
    ) {
        attributes.put(key, value)
    }
}
