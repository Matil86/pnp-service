package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
class GeneFunkClass extends BaseCharacterClass {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long id;

    String name;

    Integer level = 1;

    public void increaseLevel(Integer level) {
        this.level += level;
    }
}
