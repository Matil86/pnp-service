package de.hipp.pnp.api.converter;

import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class HashMapConverter extends BaseConverter<Map<String, String>> {

    public HashMapConverter() {
        this.type = new HashMap<String, String>();
    }
}
