package de.hipp.pnp.base.entity

import de.hipp.pnp.base.fivee.Feature5e
import jakarta.persistence.CascadeType
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
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
    @OneToMany(targetEntity = Feature5e::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var features: MutableList<Feature5e> = mutableListOf<Feature5e>()
) : Serializable {
    constructor() : this(
        name = "",
        description = "",
        attributes = emptyMap(),
        features = mutableListOf<Feature5e>()
    )
}