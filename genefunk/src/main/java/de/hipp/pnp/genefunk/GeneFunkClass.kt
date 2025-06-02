package de.hipp.pnp.genefunk

import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class GeneFunkClass : BaseCharacterClass() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    override var name: String? = ""

    override var level: Int? = 1
}
