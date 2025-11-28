package de.hipp.pnp.base.entity

import de.hipp.pnp.base.fivee.Feature5e
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
data class CharacterSpeciesEntity
    @ConstructorBinding
    constructor(
        var name: String = "",
        var description: String = "",
        var attributes: Map<String, String> = emptyMap(),
        var features: MutableList<Feature5e> = mutableListOf<Feature5e>(),
    ) : Serializable {
        constructor() : this(
            name = "",
            description = "",
            attributes = emptyMap(),
            features = mutableListOf<Feature5e>(),
        )
    }
