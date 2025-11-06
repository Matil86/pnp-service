package de.hipp.pnp.genefunk

import de.hipp.pnp.api.fivee.abstracts.Species
import de.hipp.pnp.base.fivee.Feature5e
import jakarta.annotation.Nullable
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class GeneFunkGenome : Species() {
    @Enumerated(EnumType.ORDINAL)
    var genomeType: GeneFunkGenomeType? = GeneFunkGenomeType.UNKNOWN

    @Id
    override var name: String? = ""

    @Column(columnDefinition = "TEXT")
    override var description: String? = ""

    @get:Nullable
    @Nullable
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var features: MutableSet<Feature5e?>? = HashSet<Feature5e?>()

    fun addFeature(feature5e: Feature5e?) {
        features!!.add(feature5e)
    }

}
