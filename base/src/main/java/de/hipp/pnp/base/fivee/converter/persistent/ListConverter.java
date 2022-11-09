package de.hipp.pnp.base.fivee.converter.persistent;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class ListConverter extends BaseConverter<List<Object>> {

  public ListConverter() {
    this.type = new ArrayList<Object>();
  }
}
