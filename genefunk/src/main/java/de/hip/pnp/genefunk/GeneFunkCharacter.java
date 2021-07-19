package de.hip.pnp.genefunk;

import de.hipp.pnp.Attribute5e;
import de.hipp.pnp.constants.AttributeConstants;
import de.hipp.pnp.interfaces.I5ECharacter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("JpaAttributeTypeInspection")
public class GeneFunkCharacter implements I5ECharacter {
    @Id
    @GeneratedValue
    Integer id;

    String firstName;
    String lastName;

    int level = 1;

    Attribute5e strength;
    Attribute5e dexterity;
    Attribute5e constitution;
    Attribute5e intelligence;
    Attribute5e wisdom;
    Attribute5e charisma;

    @ManyToOne
    GeneFunkGenome race;

    @ManyToMany
    List<GeneFunkClass> characterClasses = new ArrayList<>();

    void initialize() {
        applyBaseValues(race.getAttributes());
    }

    private void applyBaseValues(Map<String, Object> attributeChanges) {
        setMaxValues(attributeChanges);
        if (attributeChanges.containsKey(AttributeConstants.STRENGTH)) {
            this.strength.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.STRENGTH).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.DEXTERITY)) {
            this.dexterity.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.DEXTERITY).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.CONSTITUTION)) {
            this.constitution.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.CONSTITUTION).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.INTELLIGENCE)) {
            this.intelligence.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.INTELLIGENCE).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.WISDOM)) {
            this.wisdom.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.WISDOM).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.CHARISMA)) {
            this.charisma.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.CHARISMA).toString()));
        }
    }

    private void setMaxValues(Map<String, Object> attributeChanges) {
        if (attributeChanges.containsKey(AttributeConstants.STRENGTH_MAX)) {
            this.strength.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.STRENGTH_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.DEXTERITY_MAX)) {
            this.dexterity.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.DEXTERITY_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.CONSTITUTION_MAX)) {
            this.constitution.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.CONSTITUTION_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.INTELLIGENCE_MAX)) {
            this.intelligence.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.INTELLIGENCE_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.WISDOM_MAX)) {
            this.wisdom.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.WISDOM_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.CHARISMA_MAX)) {
            this.charisma.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.CHARISMA_MAX).toString()));
        }
    }

    void addClass(GeneFunkClass addClass) {
        int index = this.characterClasses.indexOf(addClass);
        if (index != -1) {
            this.characterClasses.get(index).increaseLevel(1);
        } else {
            this.characterClasses.add(addClass);
        }
    }
}
