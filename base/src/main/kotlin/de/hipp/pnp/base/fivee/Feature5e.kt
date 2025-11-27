package de.hipp.pnp.base.fivee

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.springframework.stereotype.Component

@Entity
@Component
data class Feature5e(
    @Column(columnDefinition = "TEXT") var label: String = "",
    @Column(columnDefinition = "TEXT") var description: String = "",
) {
    @Id
    @GeneratedValue
    private val id: Long? = null

    var availableAtLevel: Int = 0
}
