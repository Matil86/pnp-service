package de.hipp.pnp.genefunk

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass
import de.hipp.pnp.base.constants.AttributeConstants
import de.hipp.pnp.base.entity.InventoryItem
import de.hipp.pnp.base.fivee.Attribute5e

@JsonSerialize
class GeneFunkCharacter : BaseCharacter() {
    var id: Int? = null

    override var firstName: String? = null
    override var lastName: String? = null

    @JsonIgnore
    var userId: String? = null

    override var level: Int? = 1

    var strength: Attribute5e? = null
    var dexterity: Attribute5e? = null
    var constitution: Attribute5e? = null
    var intelligence: Attribute5e? = null
    var wisdom: Attribute5e? = null
    var charisma: Attribute5e? = null

    var genome: GeneFunkGenome? = null

    var money = 0

    var inventory: MutableList<InventoryItem> = mutableListOf()
    var proficientSkills: MutableList<String> = mutableListOf()

    @JsonIgnore
    fun initialize() {
        this.gameType = E5EGameTypes.GENEFUNK.value
        val genomeData = this.genome
        if (genomeData == null) {
            return
        }
        applyBaseValues(genomeData.attributes)
    }

    @JsonIgnore
    fun applyBaseValues(attributeChanges: MutableMap<String?, Int?>) {
        setMaxValues(attributeChanges)

        this.strength?.modifyValue(attributeChanges["strength"] ?: attributeChanges[AttributeConstants.STRENGTH] ?: 0)
            ?: throw IllegalStateException("Strength attribute must be initialized before applying base values")
        this.dexterity?.modifyValue(attributeChanges["dexterity"] ?: attributeChanges[AttributeConstants.DEXTERITY] ?: 0)
            ?: throw IllegalStateException("Dexterity attribute must be initialized before applying base values")
        this.constitution?.modifyValue(attributeChanges["constitution"] ?: attributeChanges[AttributeConstants.CONSTITUTION] ?: 0)
            ?: throw IllegalStateException("Constitution attribute must be initialized before applying base values")
        this.intelligence?.modifyValue(attributeChanges["intelligence"] ?: attributeChanges[AttributeConstants.INTELLIGENCE] ?: 0)
            ?: throw IllegalStateException("Intelligence attribute must be initialized before applying base values")
        this.wisdom?.modifyValue(attributeChanges["wisdom"] ?: attributeChanges[AttributeConstants.WISDOM] ?: 0)
            ?: throw IllegalStateException("Wisdom attribute must be initialized before applying base values")
        this.charisma?.modifyValue(attributeChanges["charisma"] ?: attributeChanges[AttributeConstants.CHARISMA] ?: 0)
            ?: throw IllegalStateException("Charisma attribute must be initialized before applying base values")
    }

    @JsonIgnore
    fun setMaxValues(attributeChanges: MutableMap<String?, Int?>) {
        attributeChanges["strength_max"]?.let { this.strength?.let { attr -> attr.max = it } }
            ?: attributeChanges[AttributeConstants.STRENGTH_MAX]?.let { this.strength?.let { attr -> attr.max = it } }

        attributeChanges["dexterity_max"]?.let { this.dexterity?.let { attr -> attr.max = it } }
            ?: attributeChanges[AttributeConstants.DEXTERITY_MAX]?.let { this.dexterity?.let { attr -> attr.max = it } }

        attributeChanges["constitution_max"]?.let { this.constitution?.let { attr -> attr.max = it } }
            ?: attributeChanges[AttributeConstants.CONSTITUTION_MAX]?.let { this.constitution?.let { attr -> attr.max = it } }

        attributeChanges["intelligence_max"]?.let { this.intelligence?.let { attr -> attr.max = it } }
            ?: attributeChanges[AttributeConstants.INTELLIGENCE_MAX]?.let { this.intelligence?.let { attr -> attr.max = it } }

        attributeChanges["wisdom_max"]?.let { this.wisdom?.let { attr -> attr.max = it } }
            ?: attributeChanges[AttributeConstants.WISDOM_MAX]?.let { this.wisdom?.let { attr -> attr.max = it } }

        attributeChanges["charisma_max"]?.let { this.charisma?.let { attr -> attr.max = it } }
            ?: attributeChanges[AttributeConstants.CHARISMA_MAX]?.let { this.charisma?.let { attr -> attr.max = it } }
    }

    @JsonIgnore
    fun addClass(addClass: GeneFunkClassEntity) {
        val classes = this.characterClasses ?: emptySet<BaseCharacterClass?>().toMutableSet()
        if (classes.contains(addClass)) {
            classes
                .stream()
                .filter { value: BaseCharacterClass? -> value == addClass }
                .forEach { charClass: BaseCharacterClass? -> charClass?.increaseLevel(1) }
        } else {
            classes.add(addClass)
        }
        this.characterClasses = classes
        proficientSkills.addAll(addClass.skills)
        addClass.startingEquipment.forEach {
            if (it.contains("¥")) {
                addMoney(amountString = it)
            } else {
                var item = inventory.find { item -> item.name == it }
                if (item == null) {
                    item =
                        InventoryItem().apply {
                            name = it
                        }
                }
                item.amount += 1
                inventory.add(item)
            }
        }
    }

    @JsonIgnore
    fun addMoney(amountString: String) {
        val amount =
            amountString
                .replace("¥", "")
                .replace(",", "")
                .replace(".", "")
                .replace(":", "")
                .toIntOrNull() ?: 0
        money += amount
    }
}
