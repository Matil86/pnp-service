package de.hipp.pnp.api.fivee.interfaces

import de.hipp.pnp.api.fivee.abstracts.BaseCharacter

interface FiveECharacterService<T : BaseCharacter> {
    fun getAllCharacters(userId: String?): MutableList<T>

    fun generate(): T?
}
