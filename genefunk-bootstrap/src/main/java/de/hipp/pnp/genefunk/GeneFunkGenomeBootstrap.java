package de.hipp.pnp.genefunk;

import de.hipp.pnp.base.fivee.Feature5e;
import de.hipp.pnp.base.constants.AttributeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class GeneFunkGenomeBootstrap {

    public static final String COMPANION = "Companion";
    public static final String COELHOMORTOS = "Coelhomortos";
    public static final String CANARY = "Canary";
    private final GeneFunkGenomeRepository repository;
    private final GeneFunkFeatureRepository featureRepository;

    public GeneFunkGenomeBootstrap(GeneFunkGenomeRepository repository, GeneFunkFeatureRepository featureRepository) {
        this.repository = repository;
        this.featureRepository = featureRepository;
        initialize();
    }

    protected void initialize() {
        this.repository.save(this.initializeCompanions());
        this.repository.save(this.initializeCoelhomortos());
        this.repository.save(this.initializeCanary());
    }

    private GeneFunkGenome initializeCompanions() {
        GeneFunkGenome companion = new GeneFunkGenome();
        companion.setName(COMPANION);
        companion.setDescription("companion.description");

        companion.addAttributeChange(AttributeConstants.INTELLIGENCE, 2);
        companion.addAttributeChange(AttributeConstants.INTELLIGENCE_MAX, 22);
        companion.addAttributeChange(AttributeConstants.WISDOM, 2);
        companion.addAttributeChange(AttributeConstants.WISDOM_MAX, 22);

        companion.addFeature(getFeature("chemicalDependence.label", "chemicalDependence.description"));
        companion.addFeature(getFeature("enchanting.label", "enchanting.description"));
        companion.addFeature(getFeature("performanceArtist.label", "performanceArtist.description"));
        companion.addFeature(getFeature("pheromones.label", "pheromones.description"));

        return companion;
    }

    private GeneFunkGenome initializeCoelhomortos() {
        GeneFunkGenome coelhomortos = new GeneFunkGenome();
        coelhomortos.setName(COELHOMORTOS);
        coelhomortos.setDescription("coelhomortos.description");

        coelhomortos.addAttributeChange(AttributeConstants.STRENGTH, 3);
        coelhomortos.addAttributeChange(AttributeConstants.STRENGTH_MAX, 22);
        coelhomortos.addAttributeChange(AttributeConstants.DEXTERITY, 3);
        coelhomortos.addAttributeChange(AttributeConstants.DEXTERITY_MAX, 22);

        coelhomortos.addFeature(getFeature("athleticism.label", "athleticism.description"));
        coelhomortos.addFeature(getFeature("blindLoyalty.label", "blindLoyalty.description"));
        coelhomortos.addFeature(getFeature("dangerSense.label", "dangerSense.description"));
        coelhomortos.addFeature(getFeature("pactTactics.label", "pactTactics.description"));
        coelhomortos.addFeature(getFeature("preyInstincts.label", "preyInstincts.description"));

        return coelhomortos;
    }

    private GeneFunkGenome initializeCanary() {
        GeneFunkGenome canary = new GeneFunkGenome();
        canary.setName(CANARY);
        canary.setDescription("canary.description");

        canary.addAttributeChange(AttributeConstants.STRENGTH, 4);
        canary.addAttributeChange(AttributeConstants.CONSTITUTION, 4);
        canary.addAttributeChange(AttributeConstants.CONSTITUTION_MAX, 24);
        canary.addAttributeChange(AttributeConstants.STRENGTH_MAX, 24);

        canary.addFeature(getFeature("healingFactor.label", "healingFactor.description"));
        canary.addFeature(getFeature("toxicResilience.label", "toxicResilience.description"));
        canary.addFeature(getFeature("toughAsNails.label", "toughAsNails.description"));
        canary.addFeature(getFeature("bioluminescence.label", "bioluminescence.description"));
        canary.addFeature(getFeature("musk.label", "musk.description"));
        return canary;
    }


    private Feature5e getFeature(String label, String description) {
        Optional<Feature5e> itemFound = this.featureRepository.findByLabel(label);
        return itemFound.orElseGet(() -> featureRepository.saveAndFlush(new Feature5e(label, description)));
    }
}
