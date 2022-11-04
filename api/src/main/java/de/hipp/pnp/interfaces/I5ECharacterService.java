package de.hipp.pnp.interfaces;

import java.util.List;

public interface I5ECharacterService<T extends I5ECharacter> {

    public List<T> getAllCharacters();

    public T generate();
}
