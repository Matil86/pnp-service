package de.hipp.pnp.api.converter;

import de.hipp.pnp.api.Feature5e;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class Feature5eConverter extends BaseConverter<Feature5e> {

    public Feature5eConverter() {
        this.type = new Feature5e();
    }
}
