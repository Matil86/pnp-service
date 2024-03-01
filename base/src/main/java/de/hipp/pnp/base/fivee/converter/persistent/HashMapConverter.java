package de.hipp.pnp.base.fivee.converter.persistent;

import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
@Component
public class HashMapConverter extends BaseConverter<Map<String, Object>> {

    public HashMapConverter() {
        this.type = new HashMap<String, Object>();
    }
}
