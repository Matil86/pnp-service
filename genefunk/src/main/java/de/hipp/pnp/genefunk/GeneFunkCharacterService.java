package de.hipp.pnp.genefunk;

import de.hipp.pnp.Attribute5e;
import de.hipp.pnp.DiceRoller;
import de.hipp.pnp.constants.AttributeConstants;
import de.hipp.pnp.interfaces.I5ECharacterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class GeneFunkCharacterService implements I5ECharacterService<GeneFunkCharacter> {

    final GeneFunkCharacterRepository repository;
    final GeneFunkGenomeService genomeService;
    final GeneFunkClassService classService;

    public GeneFunkCharacterService(
            GeneFunkCharacterRepository repository,
            GeneFunkGenomeService genomeService,
            GeneFunkClassService classService) {
        this.repository = repository;
        this.genomeService = genomeService;
        this.classService = classService;
    }

    @Override
    public List<GeneFunkCharacter> getAllCharacters() {
        return repository.findAll();
    }

    @Override
    public GeneFunkCharacter generate() {
        return this.generate(1);
    }

    public GeneFunkCharacter generate(int level) {
        List<GeneFunkGenome> genomes = genomeService.getAllGenomes();
        List<GeneFunkClass> classes = classService.getAllClasses();

        GeneFunkCharacter character = new GeneFunkCharacter();
        character.setLevel(level);
        character.setStrength(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        character.setDexterity(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        character.setConstitution(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        character.setIntelligence(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        character.setWisdom(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        character.setCharisma(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        character.setRace((GeneFunkGenome) pickRandom(genomes));
        this.addClass(character, (GeneFunkClass) pickRandom(classes));
        this.initialize(character);
        return repository.save(character);
    }

    private void initialize(final GeneFunkCharacter character) {
        applyBaseValues(character, character.getRace().getAttributes());
    }

    private void applyBaseValues(final GeneFunkCharacter character, Map<String, Object> attributeChanges) {
        setMaxValues(character, attributeChanges);
        if (attributeChanges.containsKey(AttributeConstants.STRENGTH)) {
            character.strength.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.STRENGTH).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.DEXTERITY)) {
            character.dexterity.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.DEXTERITY).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.CONSTITUTION)) {
            character.constitution.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.CONSTITUTION).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.INTELLIGENCE)) {
            character.intelligence.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.INTELLIGENCE).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.WISDOM)) {
            character.wisdom.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.WISDOM).toString()));
        }

        if (attributeChanges.containsKey(AttributeConstants.CHARISMA)) {
            character.charisma.modifyValue(Integer.parseInt(attributeChanges.get(AttributeConstants.CHARISMA).toString()));
        }
    }

    private void setMaxValues(final GeneFunkCharacter character, Map<String, Object> attributeChanges) {
        if (attributeChanges.containsKey(AttributeConstants.STRENGTH_MAX)) {
            character.strength.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.STRENGTH_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.DEXTERITY_MAX)) {
            character.dexterity.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.DEXTERITY_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.CONSTITUTION_MAX)) {
            character.constitution.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.CONSTITUTION_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.INTELLIGENCE_MAX)) {
            character.intelligence.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.INTELLIGENCE_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.WISDOM_MAX)) {
            character.wisdom.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.WISDOM_MAX).toString()));
        }
        if (attributeChanges.containsKey(AttributeConstants.CHARISMA_MAX)) {
            character.charisma.setMax(Integer.parseInt(attributeChanges.get(AttributeConstants.CHARISMA_MAX).toString()));
        }
    }

    void addClass(final GeneFunkCharacter character, GeneFunkClass addClass) {
        int index = character.characterClasses.indexOf(addClass);
        if (index != -1) {
            character.characterClasses.get(index).increaseLevel(1);
        } else {
            character.characterClasses.add(addClass);
        }
    }

    private <X> Object pickRandom(List<X> list) {
        Random random = new Random();
        int randomInt = random.nextInt(list.size());
        return list.get(Math.max(randomInt, 0));
    }
}
