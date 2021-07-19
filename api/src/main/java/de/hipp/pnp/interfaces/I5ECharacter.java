package de.hipp.pnp.interfaces;

import java.util.List;

public interface I5ECharacter {

    String getFirstName();

    String getLastName();

    int getLevel();

    I5ECharacterRace getRace();

    List<? extends I5ECharacterClass> getCharacterClasses();

}
