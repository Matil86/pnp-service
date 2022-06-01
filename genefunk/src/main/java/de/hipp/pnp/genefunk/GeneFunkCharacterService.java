package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.Attribute5e;
import de.hipp.pnp.api.DiceRoller;
import de.hipp.pnp.interfaces.I5ECharacterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Transactional
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
        character.addClass((GeneFunkClass) pickRandom(classes));
        character.initialize();
        return repository.save(character);
    }

    private <X> Object pickRandom(List<X> list) {
        int randomInt = new Random().nextInt(list.size());
        return list.get(Math.max(randomInt, 0));
    }
}
