package de.hipp.pnp.api.fivee.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface FiveECharacterProducer {
    String generate(int gameType) throws JsonProcessingException;

    String getAllCharacters();
}
