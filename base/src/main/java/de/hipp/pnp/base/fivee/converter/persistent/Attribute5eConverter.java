package de.hipp.pnp.base.fivee.converter.persistent;

import de.hipp.pnp.base.fivee.Attribute5e;
import org.springframework.stereotype.Component;

import javax.persistence.Converter;

@Converter(autoApply = true)
@Component
public class Attribute5eConverter extends BaseConverter<Attribute5e> {

    public Attribute5eConverter() {
        this.type = new Attribute5e();
    }
}
