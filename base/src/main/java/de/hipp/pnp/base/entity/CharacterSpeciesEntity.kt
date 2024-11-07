package de.hipp.pnp.base.entity

import de.hipp.pnp.base.fivee.Feature5e
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class CharacterSpeciesEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var description: String,
    @ElementCollection
    var attributes: Map<String, String>,
    @ElementCollection
    var features: List<Feature5e>
)