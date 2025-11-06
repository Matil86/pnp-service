package de.hipp.pnp.genefunk

import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class GeneFunkClassEntity : BaseCharacterClass() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    override var name: String = ""
    var label: String = ""
    var description: String = ""
    var savingThrows: List<String> = emptyList()
    var startingEquipment: List<String> = emptyList()
    var skills: List<String> = emptyList()

}
