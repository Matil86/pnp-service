package de.hipp.pnp.genefunk;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneFunkClassService {

    final GeneFunkClassRepository repository;

    public GeneFunkClassService(GeneFunkClassRepository repository) {
        this.repository = repository;
    }


    public List<GeneFunkClass> getAllClasses() {
        return repository.findAll();
    }
}
