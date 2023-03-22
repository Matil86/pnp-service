package de.hipp.pnp.genefunk;

import de.hipp.pnp.base.fivee.converter.persistent.CharacterConverter;
import javax.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class GeneFunkCharacterConverter extends CharacterConverter{

  public GeneFunkCharacterConverter() {
    this.setType(new GeneFunkCharacter());
  }
}
