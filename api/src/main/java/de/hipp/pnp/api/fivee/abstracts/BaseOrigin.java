package de.hipp.pnp.api.fivee.abstracts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class BaseOrigin {
    private String name;
    private String description;
    private Map<String, Integer> attributes = new HashMap<>();

    @JsonIgnore
    public void addAttributeChange(String key, Integer value) {
        attributes.put(key, value);
    }
}
