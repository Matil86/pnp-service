package de.hipp.pnp.converter;

import de.hipp.pnp.Attribute5e;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class Attribute5eConverter extends BaseConverter<Attribute5e> {

    public Attribute5eConverter() {
        this.type = new Attribute5e();
    }
}
