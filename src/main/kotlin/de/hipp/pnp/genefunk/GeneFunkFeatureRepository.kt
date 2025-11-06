package de.hipp.pnp.genefunk

import de.hipp.pnp.base.fivee.Feature5e
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GeneFunkFeatureRepository : JpaRepository<Feature5e?, Int?> {
    @Query("select f from Feature5e f where f.label = :label")
    fun findByLabel(@Param("label") label: String?): Optional<Feature5e?>?
}
