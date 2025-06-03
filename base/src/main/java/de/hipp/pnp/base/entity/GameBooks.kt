package de.hipp.pnp.base.entity

import org.springframework.boot.context.properties.bind.ConstructorBinding

data class GameBooks @ConstructorBinding constructor(
    var name: String,
    var species: List<CharacterSpeciesEntity> = emptyList(),
    var classes: Map<String, GeneFunkClass> = emptyMap()
)