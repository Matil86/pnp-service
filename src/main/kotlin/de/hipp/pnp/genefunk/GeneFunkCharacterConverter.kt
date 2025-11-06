package de.hipp.pnp.genefunk

import de.hipp.pnp.base.converter.persistent.CharacterConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
@Component
class GeneFunkCharacterConverter : CharacterConverter() {
    init {
        this.setType(GeneFunkCharacter())
    }
}
