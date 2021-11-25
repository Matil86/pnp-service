package de.hipp.pnp.interfaces;

import java.util.Set;

public interface I5ECharacter {

    String getFirstName();

    String getLastName();

    Integer getLevel();

    I5ECharacterRace getRace();

    Set<? extends I5ECharacterClass> getCharacterClasses();

}
