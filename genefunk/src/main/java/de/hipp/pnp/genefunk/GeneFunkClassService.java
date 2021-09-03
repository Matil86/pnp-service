package de.hipp.pnp.genefunk;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class GeneFunkClassService {

    final GeneFunkClassRepository repository;
    final GeneFunkClassFactory characterClassFactory;

    public GeneFunkClassService(GeneFunkClassRepository repository, GeneFunkClassFactory characterClassFactory) {
        this.repository = repository;
        this.characterClassFactory = characterClassFactory;
    }


    public List<GeneFunkClass> getAllClasses() {
        return repository.findAll();
    }

    @PostConstruct
    private void populateData() {
        repository.saveAll(characterClassFactory.initiateClasses());
    }
}
