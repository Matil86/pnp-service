package de.hipp.pnp.base.entity

import java.io.Serializable

data class Skills(
    val choose: Int = 0,
    val from: List<String> = emptyList(),
) : Serializable