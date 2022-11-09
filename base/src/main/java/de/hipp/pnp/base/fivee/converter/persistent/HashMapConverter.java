package de.hipp.pnp.base.fivee.converter.persistent;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class HashMapConverter extends BaseConverter<Map<String, String>> {

  public HashMapConverter() {
    this.type = new HashMap<String, String>();
  }
}
