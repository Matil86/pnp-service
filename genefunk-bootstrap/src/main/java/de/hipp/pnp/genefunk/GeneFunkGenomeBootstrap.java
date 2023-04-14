package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.fivee.abstracts.Bootstrap;
import de.hipp.pnp.base.fivee.constants.AttributeConstants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GeneFunkGenomeBootstrap extends Bootstrap {

	@Override
	protected void initialize() {
		log.info(this.getClass().getName() + " initialized");
		List<GeneFunkGenome> genomes = new ArrayList<>();
		genomes.add(initializeCanary());
		genomes.add(initializeCoelhomortos());
		genomes.add(initializeCompanions());
		Hibernate.initialize(genomes);
	}

	private GeneFunkGenome initializeCompanions() {
		GeneFunkGenome companion = new GeneFunkGenome();
		companion.setName("Companion");
		companion.setDescription("companion.description");

		companion.addAttributeChange(AttributeConstants.INTELLIGENCE, 2);
		companion.addAttributeChange(AttributeConstants.INTELLIGENCE_MAX, 22);
		companion.addAttributeChange(AttributeConstants.WISDOM, 2);
		companion.addAttributeChange(AttributeConstants.WISDOM_MAX, 22);

		companion.addFeature("chemicalDependence.label", "chemicalDependence.description");
		companion.addFeature("enchanting.label", "enchanting.description");
		companion.addFeature("performanceArtist.label", "performanceArtist.description");
		companion.addFeature("pheromones.label", "pheromones.description");
		return companion;
	}

	private GeneFunkGenome initializeCoelhomortos() {
		GeneFunkGenome coelhomortos = new GeneFunkGenome();
		coelhomortos.setName("Coelhomortos");
		coelhomortos.setDescription("coelhomortos.description");

		coelhomortos.addAttributeChange(AttributeConstants.STRENGTH, 3);
		coelhomortos.addAttributeChange(AttributeConstants.STRENGTH_MAX, 22);
		coelhomortos.addAttributeChange(AttributeConstants.DEXTERITY, 3);
		coelhomortos.addAttributeChange(AttributeConstants.DEXTERITY_MAX, 22);

		coelhomortos.addFeature("athleticism.label", "athleticism.description");
		coelhomortos.addFeature("blindLoyalty.label", "blindLoyalty.description");
		coelhomortos.addFeature("dangerSense.label", "dangerSense.description");
		coelhomortos.addFeature("pactTactics.label", "pactTactics.description");
		coelhomortos.addFeature("preyInstincts.label", "preyInstincts.description");
		return coelhomortos;
	}

	private GeneFunkGenome initializeCanary() {
		GeneFunkGenome canary = new GeneFunkGenome();
		canary.setName("Canary");
		canary.setDescription("canary.description");

		canary.addAttributeChange(AttributeConstants.STRENGTH, 4);
		canary.addAttributeChange(AttributeConstants.CONSTITUTION, 4);
		canary.addAttributeChange(AttributeConstants.CONSTITUTION_MAX, 24);
		canary.addAttributeChange(AttributeConstants.STRENGTH_MAX, 24);

		canary.addFeature("healingFactor.label", "healingFactor.description");
		canary.addFeature("toxicResilience.label", "toxicResilience.description");
		canary.addFeature("toughAsNails.label", "toughAsNails.description");
		canary.addFeature("bioluminescence.label", "bioluminescence.description");
		canary.addFeature("musk.label", "musk.description");
		return canary;
	}

}
