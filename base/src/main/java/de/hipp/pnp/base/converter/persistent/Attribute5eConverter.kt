package de.hipp.pnp.base.converter.persistent

import de.hipp.pnp.base.fivee.Attribute5e
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
@Component
class Attribute5eConverter : BaseConverter<Attribute5e?>() {
    init {
        this.type = Attribute5e()
    }
}
