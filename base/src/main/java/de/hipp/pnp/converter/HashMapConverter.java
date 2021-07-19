package de.hipp.pnp.converter;

import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class HashMapConverter extends BaseConverter<Map<String, Object>> {

    public HashMapConverter() {
        this.type = new HashMap<String, Object>();
    }
}
