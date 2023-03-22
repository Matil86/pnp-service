package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.fivee.abstracts.BaseCharacterClass;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
class GeneFunkClass extends BaseCharacterClass {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long id;

    String name;

    Integer level = 1;

}
