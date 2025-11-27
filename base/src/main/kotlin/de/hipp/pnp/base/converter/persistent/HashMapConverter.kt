package de.hipp.pnp.base.converter.persistent

import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
@Component
class HashMapConverter : BaseConverter<Map<String, Any>>() {
    init {
        this.type = HashMap<String, Any>()
    }
}
