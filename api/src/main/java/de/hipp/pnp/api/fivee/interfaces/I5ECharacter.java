package de.hipp.pnp.api.fivee.interfaces;

import java.io.Serializable;
import java.util.Set;

public interface I5ECharacter extends Serializable {

    int getGameType();

    String getFirstName();

    String getLastName();

    Integer getLevel();

    I5ECharacterRace getRace();

    Set<? extends I5ECharacterClass> getCharacterClasses();

}
