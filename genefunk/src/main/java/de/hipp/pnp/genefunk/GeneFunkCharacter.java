package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.Attribute5e;
import de.hipp.pnp.api.constants.AttributeConstants;
import de.hipp.pnp.interfaces.I5ECharacter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("JpaAttributeTypeInspection")
@Entity
public class GeneFunkCharacter implements I5ECharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String firstName;
    String lastName;

    Integer level = 1;

    @OneToOne(targetEntity = Attribute5e.class,fetch = FetchType.EAGER)
    Attribute5e strength;
    @OneToOne(targetEntity = Attribute5e.class,fetch = FetchType.EAGER)
    Attribute5e dexterity;
    @OneToOne(targetEntity = Attribute5e.class,fetch = FetchType.EAGER)
    Attribute5e constitution;
    @OneToOne(targetEntity = Attribute5e.class,fetch = FetchType.EAGER)
    Attribute5e intelligence;
    @OneToOne(targetEntity = Attribute5e.class,fetch = FetchType.EAGER)
    Attribute5e wisdom;
    @OneToOne(targetEntity = Attribute5e.class,fetch = FetchType.EAGER)
    Attribute5e charisma;

    @OneToOne
    GeneFunkGenome race;

    @ElementCollection(targetClass = GeneFunkClass.class, fetch = FetchType.EAGER)
    Set<GeneFunkClass> characterClasses = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Attribute5e getStrength() {
        return strength;
    }

    public void setStrength(Attribute5e strength) {
        this.strength = strength;
    }

    public Attribute5e getDexterity() {
        return dexterity;
    }

    public void setDexterity(Attribute5e dexterity) {
        this.dexterity = dexterity;
    }

    public Attribute5e getConstitution() {
        return constitution;
    }

    public void setConstitution(Attribute5e constitution) {
        this.constitution = constitution;
    }

    public Attribute5e getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(Attribute5e intelligence) {
        this.intelligence = intelligence;
    }

    public Attribute5e getWisdom() {
        return wisdom;
    }

    public void setWisdom(Attribute5e wisdom) {
        this.wisdom = wisdom;
    }

    public Attribute5e getCharisma() {
        return charisma;
    }

    public void setCharisma(Attribute5e charisma) {
        this.charisma = charisma;
    }

    @Override
    public GeneFunkGenome getRace() {
        return race;
    }

    public void setRace(GeneFunkGenome race) {
        this.race = race;
    }

    @Override
    public Set<GeneFunkClass> getCharacterClasses() {
        return characterClasses;
    }

    public void setCharacterClasses(Set<GeneFunkClass> characterClasses) {
        this.characterClasses = characterClasses;
    }

    void initialize() {
        applyBaseValues(this.getRace().getAttributes());
    }

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

    void addClass(GeneFunkClass addClass) {
        boolean index = this.characterClasses.contains(addClass);
        if (index) {
            this.characterClasses.stream().filter((value) -> value.equals(addClass)).forEach((charClass) -> charClass.increaseLevel(1));
        } else {
            this.characterClasses.add(addClass);
        }
    }
}
