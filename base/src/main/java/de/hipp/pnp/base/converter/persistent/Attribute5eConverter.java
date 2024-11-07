package de.hipp.pnp.base.converter.persistent;

import de.hipp.pnp.base.fivee.Attribute5e;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class Attribute5eConverter extends BaseConverter<Attribute5e> {

    public Attribute5eConverter() {
        this.type = new Attribute5e();
    }
}
