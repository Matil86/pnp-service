package de.hipp.pnp.api.fivee.interfaces;

import java.util.List;

public interface FiveECharacterProducer {
	String generate(int gameType);

	List<Object> getAllCharacters();
}
