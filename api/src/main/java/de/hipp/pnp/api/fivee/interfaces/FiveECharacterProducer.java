package de.hipp.pnp.api.fivee.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter;

import java.util.List;

public interface FiveECharacterProducer {
    BaseCharacter generate(int gameType) throws JsonProcessingException;

    List<BaseCharacter> getAllCharacters();
}
