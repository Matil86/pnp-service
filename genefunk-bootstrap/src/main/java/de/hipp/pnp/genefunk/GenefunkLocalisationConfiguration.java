package de.hipp.pnp.genefunk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;

@Configuration
@PropertySource("classpath:locales/genefunk_en_US.properties")
public class GenefunkLocalisationConfiguration {

    @Value("${localize}")
    HashMap<String,String> localisationMap = new HashMap<>();

    public HashMap<String,String> getLocalisationMap() {
        return localisationMap;
    }
}
