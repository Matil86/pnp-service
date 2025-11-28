package de.hipp.pnp.genefunk

import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass

class GeneFunkClassEntity : BaseCharacterClass() {
    var id: Long = 0

    override var name: String = ""
    var label: String = ""
    var description: String = ""
    var savingThrows: List<String> = emptyList()
    var startingEquipment: List<String> = emptyList()
    var skills: List<String> = emptyList()
}
