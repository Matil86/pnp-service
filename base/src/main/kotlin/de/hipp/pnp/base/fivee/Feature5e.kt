package de.hipp.pnp.base.fivee

import org.springframework.stereotype.Component

@Component
data class Feature5e(
    var label: String = "",
    var description: String = "",
) {
    private val id: Long? = null

    var availableAtLevel: Int = 0
}
