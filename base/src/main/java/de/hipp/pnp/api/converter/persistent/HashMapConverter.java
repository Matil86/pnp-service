package de.hipp.pnp.api.converter.persistent;

import org.springframework.stereotype.Component;

import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
@Component
public class HashMapConverter extends BaseConverter<Map<String, String>> {

    public HashMapConverter() {
        this.type = new HashMap<String, String>();
    }
}
