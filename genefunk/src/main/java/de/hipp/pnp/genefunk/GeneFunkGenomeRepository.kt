package de.hipp.pnp.genefunk

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GeneFunkGenomeRepository : JpaRepository<GeneFunkGenome?, Int?> {
    fun findByName(name: String?): Optional<GeneFunkGenome?>?
}
