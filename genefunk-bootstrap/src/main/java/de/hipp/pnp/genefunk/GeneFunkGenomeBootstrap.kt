package de.hipp.pnp.genefunk

import de.hipp.pnp.base.constants.AttributeConstants
import de.hipp.pnp.base.fivee.Feature5e
import org.springframework.stereotype.Component

@Component
open class GeneFunkGenomeBootstrap(
    private val repository: GeneFunkGenomeRepository,
    private val featureRepository: GeneFunkFeatureRepository
) {

    init {
        initialize()
    }

    private fun initialize() {
        repository.save(this.initializeCompanions())
        repository.save(this.initializeCoelhomortos())
        repository.save(this.initializeCanary())
    }

    private fun initializeCompanions(): GeneFunkGenome {
        val companion = GeneFunkGenome()
        companion.setName(COMPANION)
        companion.setDescription("companion.description")

        companion.addAttributeChange(AttributeConstants.INTELLIGENCE, 2)
        companion.addAttributeChange(AttributeConstants.INTELLIGENCE_MAX, 22)
        companion.addAttributeChange(AttributeConstants.WISDOM, 2)
        companion.addAttributeChange(AttributeConstants.WISDOM_MAX, 22)

        companion.addFeature(getFeature("chemicalDependence.label", "chemicalDependence.description"))
        companion.addFeature(getFeature("enchanting.label", "enchanting.description"))
        companion.addFeature(getFeature("performanceArtist.label", "performanceArtist.description"))
        companion.addFeature(getFeature("pheromones.label", "pheromones.description"))

        return companion
    }

    private fun initializeCoelhomortos(): GeneFunkGenome {
        val coelhomortos = GeneFunkGenome()
        coelhomortos.setName(COELHOMORTOS)
        coelhomortos.setDescription("coelhomortos.description")

        coelhomortos.addAttributeChange(AttributeConstants.STRENGTH, 3)
        coelhomortos.addAttributeChange(AttributeConstants.STRENGTH_MAX, 22)
        coelhomortos.addAttributeChange(AttributeConstants.DEXTERITY, 3)
        coelhomortos.addAttributeChange(AttributeConstants.DEXTERITY_MAX, 22)

        coelhomortos.addFeature(getFeature("athleticism.label", "athleticism.description"))
        coelhomortos.addFeature(getFeature("blindLoyalty.label", "blindLoyalty.description"))
        coelhomortos.addFeature(getFeature("dangerSense.label", "dangerSense.description"))
        coelhomortos.addFeature(getFeature("pactTactics.label", "pactTactics.description"))
        coelhomortos.addFeature(getFeature("preyInstincts.label", "preyInstincts.description"))

        return coelhomortos
    }

    private fun initializeCanary(): GeneFunkGenome {
        val canary = GeneFunkGenome()
        canary.setName(CANARY)
        canary.setDescription("canary.description")

        canary.addAttributeChange(AttributeConstants.STRENGTH, 4)
        canary.addAttributeChange(AttributeConstants.CONSTITUTION, 4)
        canary.addAttributeChange(AttributeConstants.CONSTITUTION_MAX, 24)
        canary.addAttributeChange(AttributeConstants.STRENGTH_MAX, 24)

        canary.addFeature(getFeature("healingFactor.label", "healingFactor.description"))
        canary.addFeature(getFeature("toxicResilience.label", "toxicResilience.description"))
        canary.addFeature(getFeature("toughAsNails.label", "toughAsNails.description"))
        canary.addFeature(getFeature("bioluminescence.label", "bioluminescence.description"))
        canary.addFeature(getFeature("musk.label", "musk.description"))
        return canary
    }


    private fun getFeature(label: String, description: String): Feature5e {
        val itemFound = featureRepository.findByLabel(label)
        return itemFound.orElseGet { featureRepository.saveAndFlush(Feature5e(label, description)) }
    }

    companion object {
        const val COMPANION: String = "Companion"
        const val COELHOMORTOS: String = "Coelhomortos"
        const val CANARY: String = "Canary"
    }
}
