package de.hipp.pnp.base.fivee.converter.persistent;

import org.springframework.stereotype.Component;

import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter(autoApply = true)
@Component
public class ListConverter extends BaseConverter<List<Object>> {

    public ListConverter() {
        this.type = new ArrayList<Object>();
    }
}
