package de.hipp.pnp.genefunk

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass
import de.hipp.pnp.base.constants.AttributeConstants
import de.hipp.pnp.base.entity.InventoryItem
import de.hipp.pnp.base.fivee.Attribute5e
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne


@Entity
@JsonSerialize
class GeneFunkCharacter : BaseCharacter() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    var genome: GeneFunkGenome? = null

    var money = 0

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var inventory: MutableList<InventoryItem> = mutableListOf()
    var proficientSkills: MutableList<String> = mutableListOf()

    @JsonIgnore
    fun initialize() {
        this.gameType = E5EGameTypes.GENEFUNK.value
        if (this.genome == null) {
            return
        }
        applyBaseValues(this.genome!!.attributes)
    }

    @JsonIgnore
    fun applyBaseValues(attributeChanges: MutableMap<String?, Int?>) {
        setMaxValues(attributeChanges)

        this.strength!!.modifyValue(attributeChanges[AttributeConstants.STRENGTH] ?: 0)
        this.dexterity!!.modifyValue(attributeChanges[AttributeConstants.DEXTERITY] ?: 0)
        this.constitution!!.modifyValue(attributeChanges[AttributeConstants.CONSTITUTION] ?: 0)
        this.intelligence!!.modifyValue(attributeChanges[AttributeConstants.INTELLIGENCE] ?: 0)
        this.wisdom!!.modifyValue(attributeChanges[AttributeConstants.WISDOM] ?: 0)
        this.charisma!!.modifyValue(attributeChanges[AttributeConstants.CHARISMA] ?: 0)
    }

    @JsonIgnore
    fun setMaxValues(attributeChanges: MutableMap<String?, Int?>) {
        if (attributeChanges.containsKey(AttributeConstants.STRENGTH_MAX)) {
            this.strength!!.max = attributeChanges.get(AttributeConstants.STRENGTH_MAX)!!
        }
        if (attributeChanges.containsKey(AttributeConstants.DEXTERITY_MAX)) {
            this.dexterity!!.max = attributeChanges.get(AttributeConstants.DEXTERITY_MAX)!!
        }
        if (attributeChanges.containsKey(AttributeConstants.CONSTITUTION_MAX)) {
            this.constitution!!.max = attributeChanges.get(AttributeConstants.CONSTITUTION_MAX)!!
        }
        if (attributeChanges.containsKey(AttributeConstants.INTELLIGENCE_MAX)) {
            this.intelligence!!.max = attributeChanges.get(AttributeConstants.INTELLIGENCE_MAX)!!
        }
        if (attributeChanges.containsKey(AttributeConstants.WISDOM_MAX)) {
            this.wisdom!!.max = attributeChanges.get(AttributeConstants.WISDOM_MAX)!!
        }
        if (attributeChanges.containsKey(AttributeConstants.CHARISMA_MAX)) {
            this.charisma!!.max = attributeChanges.get(AttributeConstants.CHARISMA_MAX)!!
        }
    }

    @JsonIgnore
    fun addClass(addClass: GeneFunkClassEntity) {
        val classes = this.characterClasses ?: emptySet<BaseCharacterClass?>().toMutableSet()
        if (classes.contains(addClass)) {
            classes.stream().filter { value: BaseCharacterClass? -> value == addClass }
                .forEach { charClass: BaseCharacterClass? -> charClass!!.increaseLevel(1) }
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
                    item = InventoryItem().apply {
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
        val amount = amountString
            .replace("¥", "")
            .replace(",", "")
            .replace(".", "")
            .replace(":", "")
            .toIntOrNull() ?: 0
        money += amount
    }
}
