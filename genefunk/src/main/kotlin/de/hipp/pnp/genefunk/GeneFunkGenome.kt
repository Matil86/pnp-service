package de.hipp.pnp.genefunk

import de.hipp.pnp.api.fivee.abstracts.Species
import de.hipp.pnp.base.fivee.Feature5e

class GeneFunkGenome : Species() {
    var genomeType: GeneFunkGenomeType? = GeneFunkGenomeType.UNKNOWN

    override var name: String? = ""

    override var description: String? = ""

    var features: MutableSet<Feature5e?>? = HashSet<Feature5e?>()

    fun addFeature(feature5e: Feature5e?) {
        val featureSet = this.features ?: HashSet<Feature5e?>().also { this.features = it }
        featureSet.add(feature5e)
    }
}
