package de.hip.pnp.genefunk;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
class GeneFunkClassFactory {
    public Collection<? extends GeneFunkClass> initiateClasses() {
        List<GeneFunkClass> returnList = new ArrayList<>();
        returnList.add(this.initiateBiohacker());
        returnList.add(this.initiateGunfighter());
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
