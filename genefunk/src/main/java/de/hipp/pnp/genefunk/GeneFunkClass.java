package de.hipp.pnp.genefunk;

import de.hipp.pnp.interfaces.I5ECharacterClass;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
class GeneFunkClass implements I5ECharacterClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String name;

    Integer level = 1;

    public void increaseLevel(Integer level) {
        this.level += level;
    }
}
