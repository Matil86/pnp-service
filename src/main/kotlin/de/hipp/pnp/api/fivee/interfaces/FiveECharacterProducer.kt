package de.hipp.pnp.api.fivee.interfaces

import com.fasterxml.jackson.core.JsonProcessingException
import de.hipp.pnp.api.fivee.abstracts.BaseCharacter

interface FiveECharacterProducer {
    @Throws(JsonProcessingException::class)
    fun generate(gameType: Int): String?

    fun allCharacters(): MutableList<BaseCharacter?>?
}
