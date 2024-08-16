package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.hipp.pnp.api.fivee.E5EGameTypes;
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter;
import de.hipp.pnp.base.constants.AttributeConstants;
import de.hipp.pnp.base.fivee.Attribute5e;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;

@Entity
@Setter
@Getter
@JsonSerialize
public class GeneFunkCharacter extends BaseCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String firstName;
    String lastName;
    String userId;

    Integer level = 1;

    Attribute5e strength;
    Attribute5e dexterity;
    Attribute5e constitution;
    Attribute5e intelligence;
    Attribute5e wisdom;
    Attribute5e charisma;

    @ManyToOne
    GeneFunkGenome genome;

    @JsonIgnore
    void initialize() {
        this.setGameType(E5EGameTypes.GENEFUNK.getValue());
        if (this.getGenome() == null) {
            return;
        }
        applyBaseValues(this.getGenome().getAttributes());
    }

    @JsonIgnore
    void applyBaseValues(Map<String, Integer> attributeChanges) {
        setMaxValues(attributeChanges);
        if (attributeChanges.containsKey(AttributeConstants.STRENGTH)) {
            this.strength.modifyValue(attributeChanges.get(AttributeConstants.STRENGTH));
        }

        if (attributeChanges.containsKey(AttributeConstants.DEXTERITY)) {
            this.dexterity.modifyValue(attributeChanges.get(AttributeConstants.DEXTERITY));
        }

        if (attributeChanges.containsKey(AttributeConstants.CONSTITUTION)) {
            this.constitution.modifyValue(attributeChanges.get(AttributeConstants.CONSTITUTION));
        }

        if (attributeChanges.containsKey(AttributeConstants.INTELLIGENCE)) {
            this.intelligence.modifyValue(attributeChanges.get(AttributeConstants.INTELLIGENCE));
        }

        if (attributeChanges.containsKey(AttributeConstants.WISDOM)) {
            this.wisdom.modifyValue(attributeChanges.get(AttributeConstants.WISDOM));
        }

        if (attributeChanges.containsKey(AttributeConstants.CHARISMA)) {
            this.charisma.modifyValue(attributeChanges.get(AttributeConstants.CHARISMA));
        }
    }

    @JsonIgnore
    void setMaxValues(Map<String, Integer> attributeChanges) {
        if (attributeChanges.containsKey(AttributeConstants.STRENGTH_MAX)) {
            this.strength.setMax(attributeChanges.get(AttributeConstants.STRENGTH_MAX));
        }
        if (attributeChanges.containsKey(AttributeConstants.DEXTERITY_MAX)) {
            this.dexterity.setMax(attributeChanges.get(AttributeConstants.DEXTERITY_MAX));
        }
        if (attributeChanges.containsKey(AttributeConstants.CONSTITUTION_MAX)) {
            this.constitution.setMax(attributeChanges.get(AttributeConstants.CONSTITUTION_MAX));
        }
        if (attributeChanges.containsKey(AttributeConstants.INTELLIGENCE_MAX)) {
            this.intelligence.setMax(attributeChanges.get(AttributeConstants.INTELLIGENCE_MAX));
        }
        if (attributeChanges.containsKey(AttributeConstants.WISDOM_MAX)) {
            this.wisdom.setMax(attributeChanges.get(AttributeConstants.WISDOM_MAX));
        }
        if (attributeChanges.containsKey(AttributeConstants.CHARISMA_MAX)) {
            this.charisma.setMax(attributeChanges.get(AttributeConstants.CHARISMA_MAX));
        }
    }

    @JsonIgnore
    void addClass(GeneFunkClass addClass) {
        if (this.characterClasses == null) {
            this.characterClasses = new HashSet<>();
        }
        boolean index = this.characterClasses.contains(addClass);
        if (index) {
            this.characterClasses.stream().filter(value -> value.equals(addClass)).forEach(charClass -> charClass.increaseLevel(1));
        } else {
            this.characterClasses.add(addClass);
        }
    }
}
