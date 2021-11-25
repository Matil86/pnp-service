package de.hipp.pnp.genefunk;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
class GeneFunkClassFactory {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public Collection<? extends GeneFunkClass> initiateClasses() {
        List<GeneFunkClass> returnList = new ArrayList<>();
        returnList.add(this.initiateBiohacker());
        returnList.add(this.initiateGunfighter());
        Hibernate.initialize(returnList);
        return returnList;
    }


    private GeneFunkClass initiateBiohacker() {
        GeneFunkClass biohacker = new GeneFunkClass();
        biohacker.setName("Biohacker");
        return biohacker;
    }

    private GeneFunkClass initiateGunfighter() {
        GeneFunkClass biohacker = new GeneFunkClass();
        biohacker.setName("Gunfighter");
        return biohacker;
    }
}
