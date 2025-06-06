package de.hipp.pnp.genefunk

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GeneFunkClassRepository : JpaRepository<GeneFunkClassEntity?, Int?> {
    fun findByName(name: String?): Optional<GeneFunkClassEntity?>?
}
