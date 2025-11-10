package de.hipp.pnp.base.converter.persistent

import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
@Component
class CharacterConverter : BaseConverter<BaseCharacter>() {
    init {
        this.type = BaseCharacter()
    }
}
