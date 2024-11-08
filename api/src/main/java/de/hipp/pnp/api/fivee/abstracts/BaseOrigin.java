package de.hipp.pnp.api.fivee.abstracts;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseOrigin {
    private String name;
    private String description;
    private Map<String, Integer> attributes = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Integer> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Integer> attributes) {
        this.attributes = attributes;
    }

    @JsonIgnore
    public void addAttributeChange(String key, Integer value) {
        attributes.put(key, value);
    }
}
