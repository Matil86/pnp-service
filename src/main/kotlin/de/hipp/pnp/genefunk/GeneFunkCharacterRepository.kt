package de.hipp.pnp.genefunk

import org.springframework.data.jpa.repository.JpaRepository

interface GeneFunkCharacterRepository : JpaRepository<GeneFunkCharacter?, Int?> {
    fun findByUserId(userId: String?): MutableList<GeneFunkCharacter?>?
}
