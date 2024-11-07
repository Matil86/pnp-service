package de.hipp.pnp.base.converter.persistent

import de.hipp.pnp.base.fivee.Feature5e
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
@Component
class Feature5eConverter : BaseConverter<Feature5e?>() {
    init {
        this.type = Feature5e()
    }
}
