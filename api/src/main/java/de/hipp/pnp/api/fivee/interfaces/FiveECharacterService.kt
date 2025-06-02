package de.hipp.pnp.api.fivee.interfaces;

import de.hipp.pnp.api.fivee.abstracts.BaseCharacter;

import java.util.List;

public interface FiveECharacterService<T extends BaseCharacter> {

    public List<T> getAllCharacters(String userId);

    public T generate();
}
