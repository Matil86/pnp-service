package de.hipp.pnp.genefunk

import org.springframework.data.jpa.repository.JpaRepository

// Spring Boot 4 requires non-nullable type parameters for JpaRepository<T, ID>
interface GeneFunkCharacterRepository : JpaRepository<GeneFunkCharacter, Int> {
    fun findByUserId(userId: String?): MutableList<GeneFunkCharacter>
}
