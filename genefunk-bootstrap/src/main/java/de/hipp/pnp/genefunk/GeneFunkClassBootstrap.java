package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.fivee.abstracts.Bootstrap;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class GeneFunkClassBootstrap extends Bootstrap {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    protected void initialize() {
        List<GeneFunkClass> returnList = new ArrayList<>();
        returnList.add(this.initiateBiohacker());
        returnList.add(this.initiateGunfighter());
        Hibernate.initialize(returnList);
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
