package de.hipp.pnp.api.fivee.interfaces;

import java.util.List;

public interface I5ECharacterService<T extends I5ECharacter> {

    public List<T> getAllCharacters();

    public T generate();
}
