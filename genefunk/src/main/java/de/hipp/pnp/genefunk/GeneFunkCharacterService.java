package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.fivee.interfaces.FiveECharacterService;
import de.hipp.pnp.base.fivee.Attribute5e;
import de.hipp.pnp.base.fivee.DiceRoller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@Transactional
public class GeneFunkCharacterService implements FiveECharacterService<GeneFunkCharacter> {

    final GeneFunkCharacterRepository repository;
    final GeneFunkGenomeService genomeService;
    final GeneFunkClassService classService;
    private final Random random = new Random();

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
        return this.generate(new GeneFunkCharacter());
    }

    public GeneFunkCharacter generate(GeneFunkCharacter character) {
        List<GeneFunkGenome> genomes = genomeService.getAllGenomes();
        List<GeneFunkClass> classes = classService.getAllClasses();

        if (Objects.isNull(character)) {
            character = new GeneFunkCharacter();
        }
        if (Objects.isNull(character.getLevel())) {
            character.setLevel(1);
        }
        if (Objects.isNull(character.getStrength())) {
            character.setStrength(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        }
        if (Objects.isNull(character.getDexterity())) {
            character.setDexterity(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        }
        if (Objects.isNull(character.getConstitution())) {
            character.setConstitution(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        }
        if (Objects.isNull(character.getIntelligence())) {
            character.setIntelligence(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        }
        if (Objects.isNull(character.getWisdom())) {
            character.setWisdom(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        }
        if (Objects.isNull(character.getCharisma())) {
            character.setCharisma(new Attribute5e(DiceRoller.roll(4, 6, 3, true)));
        }
        if (!genomes.isEmpty() && Objects.isNull(character.getGenome())) {
            character.setGenome((GeneFunkGenome) pickRandom(genomes));
        }
        if (!classes.isEmpty() && Objects.isNull(character.getCharacterClasses())) {
            character.addClass((GeneFunkClass) pickRandom(classes));
        }
        character.initialize();
        return repository.save(character);
    }

    private <X> Object pickRandom(List<X> list) {
        int randomInt = random.nextInt(list.size());
        return list.get(Math.max(randomInt, 0));
    }
}
