package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
class GeneFunkClass extends BaseCharacterClass {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long id;

    String name;

    Integer level = 1;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
