package de.hipp.pnp.base.fivee.converter.persistent;

import de.hipp.pnp.base.fivee.Feature5e;
import org.springframework.stereotype.Component;

import javax.persistence.Converter;

@Converter(autoApply = true)
@Component
public class Feature5eConverter extends BaseConverter<Feature5e> {

    public Feature5eConverter() {
        this.type = new Feature5e();
    }
}
