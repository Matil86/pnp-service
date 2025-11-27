package de.hipp.pnp.base.converter.persistent

import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
@Component
class ListConverter : BaseConverter<List<Any>>() {
    init {
        this.type = ArrayList<Any>()
    }
}
