package de.hipp.pnp.genefunk;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class GeneFunkGenomeService {

    final GeneFunkGenomeRepository repository;

    public GeneFunkGenomeService(GeneFunkGenomeRepository repository) {
        this.repository = repository;
    }

    public void save(Collection<? extends GeneFunkGenome> genomes) {
        repository.saveAll(genomes);
    }

    public List<GeneFunkGenome> getAllGenomes() {
        return repository.findAll();
    }
}
