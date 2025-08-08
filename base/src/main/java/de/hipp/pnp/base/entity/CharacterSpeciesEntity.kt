package de.hipp.pnp.base.entity

import de.hipp.pnp.base.fivee.Feature5e
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.stereotype.Component
import java.io.Serializable

@Entity
@Component
data class CharacterSpeciesEntity @ConstructorBinding constructor(
    @Id
    var name: String = "",
    var description: String = "",
    @ElementCollection
    var attributes: Map<String, String> = emptyMap(),
    @ElementCollection(targetClass = Feature5e::class)
    var features: List<Feature5e> = emptyList()
) : Serializable {
    constructor() : this(
        name = "",
        description = "",
        attributes = emptyMap(),
        features = emptyList()
    )
}