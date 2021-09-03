package de.hipp.pnp.genefunk;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Service
public class GeneFunkGenomeService {

    final GeneFunkGenomeRepository repository;
    final GeneFunkGenomeFactory genomeFactory;

    public GeneFunkGenomeService(GeneFunkGenomeRepository repository, GeneFunkGenomeFactory genomeFactory) {
        this.repository = repository;
        this.genomeFactory = genomeFactory;
    }

    public void save(Collection<? extends GeneFunkGenome> genomes) {
        repository.saveAll(genomes);
    }

    public List<GeneFunkGenome> getAllGenomes() {
        return repository.findAll();
    }

    @PostConstruct
    private void populateData() {
        this.save(genomeFactory.initiateAllGenomes());
    }
}
