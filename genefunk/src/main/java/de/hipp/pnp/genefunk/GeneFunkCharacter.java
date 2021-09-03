package de.hipp.pnp.genefunk;

import de.hipp.pnp.Attribute5e;
import de.hipp.pnp.interfaces.I5ECharacter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GeneFunkCharacter implements I5ECharacter {
    @Id
    @GeneratedValue
    Integer id;

    String firstName;
    String lastName;

    Integer level = 1;

    @OneToOne
    Attribute5e strength;
    @OneToOne
    Attribute5e dexterity;
    @OneToOne
    Attribute5e constitution;
    @OneToOne
    Attribute5e intelligence;
    @OneToOne
    Attribute5e wisdom;
    @OneToOne
    Attribute5e charisma;

    @OneToOne
    GeneFunkGenome race;

    @ManyToMany
    List<GeneFunkClass> characterClasses = new ArrayList<>();

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
    public List<GeneFunkClass> getCharacterClasses() {
        return characterClasses;
    }

    public void setCharacterClasses(List<GeneFunkClass> characterClasses) {
        this.characterClasses = characterClasses;
    }
}
