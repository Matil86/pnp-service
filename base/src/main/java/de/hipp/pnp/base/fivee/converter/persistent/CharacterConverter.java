package de.hipp.pnp.base.fivee.converter.persistent;

import de.hipp.pnp.api.fivee.abstracts.BaseCharacter;
import javax.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class CharacterConverter extends BaseConverter<BaseCharacter> {

  public CharacterConverter() {
    this.type = new BaseCharacter();
  }
}
