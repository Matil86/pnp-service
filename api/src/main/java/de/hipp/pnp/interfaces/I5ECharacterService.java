package de.hipp.pnp.interfaces;

import java.util.List;

public interface I5ECharacterService<t extends I5ECharacter> {

    public List<t> getAllCharacters();

    public t generate();
}
