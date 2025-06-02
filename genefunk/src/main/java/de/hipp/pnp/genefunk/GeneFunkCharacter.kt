package de.hipp.pnp.genefunk

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.hipp.pnp.api.fivee.E5EGameTypes
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter
import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass
import de.hipp.pnp.base.constants.AttributeConstants
import de.hipp.pnp.base.fivee.Attribute5e
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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
        if (attributeChanges.containsKey(AttributeConstants.STRENGTH)) {
            this.strength!!.modifyValue(attributeChanges.get(AttributeConstants.STRENGTH)!!)
        }

        if (attributeChanges.containsKey(AttributeConstants.DEXTERITY)) {
            this.dexterity!!.modifyValue(attributeChanges.get(AttributeConstants.DEXTERITY)!!)
        }

        if (attributeChanges.containsKey(AttributeConstants.CONSTITUTION)) {
            this.constitution!!.modifyValue(attributeChanges.get(AttributeConstants.CONSTITUTION)!!)
        }

        if (attributeChanges.containsKey(AttributeConstants.INTELLIGENCE)) {
            this.intelligence!!.modifyValue(attributeChanges.get(AttributeConstants.INTELLIGENCE)!!)
        }

        if (attributeChanges.containsKey(AttributeConstants.WISDOM)) {
            this.wisdom!!.modifyValue(attributeChanges.get(AttributeConstants.WISDOM)!!)
        }

        if (attributeChanges.containsKey(AttributeConstants.CHARISMA)) {
            this.charisma!!.modifyValue(attributeChanges.get(AttributeConstants.CHARISMA)!!)
        }
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
    fun addClass(addClass: GeneFunkClass?) {
        val classes = this.characterClasses ?: emptySet<BaseCharacterClass?>().toMutableSet()
        val index = classes.contains(addClass)
        if (index) {
            classes.stream().filter { value: BaseCharacterClass? -> value == addClass }
                .forEach { charClass: BaseCharacterClass? -> charClass!!.increaseLevel(1) }
        } else {
            classes.add(addClass)
        }
        this.characterClasses = classes
    }
}
