package de.hipp.pnp.base.entity

import java.io.Serializable

data class CharacterCreation(
    val customCreation: CustomCreation? = null,
    val startingEquipment: List<String>? = null,
    val savingThrows: List<String> = emptyList(),
    val skills: Skills = Skills(),
) : Serializable