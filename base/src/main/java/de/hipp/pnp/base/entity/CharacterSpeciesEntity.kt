package de.hipp.pnp.base.entity

import de.hipp.pnp.base.fivee.Feature5e
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.io.Serializable

@Entity
data class CharacterSpeciesEntity(
    @Id
    var name: String = "",
    var description: String = "",
    @ElementCollection
    var attributes: Map<String, String> = emptyMap(),
    @ElementCollection
    var features: List<Feature5e> = emptyList()
) {
    constructor() : this(
        name = "",
        description = "",
        attributes = emptyMap(),
        features = emptyList()
    )
}