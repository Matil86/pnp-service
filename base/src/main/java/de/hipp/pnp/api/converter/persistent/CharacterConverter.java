package de.hipp.pnp.api.converter.persistent;

import de.hipp.pnp.interfaces.I5ECharacter;
import de.hipp.pnp.interfaces.I5ECharacterClass;
import de.hipp.pnp.interfaces.I5ECharacterRace;
import java.util.Set;
import javax.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class CharacterConverter extends BaseConverter<I5ECharacter> {

  public CharacterConverter() {
    this.type = new I5ECharacter() {
      @Override
      public int getGameType() {
        return 0;
      }

      @Override
      public String getFirstName() {
        return null;
      }

      @Override
      public String getLastName() {
        return null;
      }

      @Override
      public Integer getLevel() {
        return null;
      }

      @Override
      public I5ECharacterRace getRace() {
        return null;
      }

      @Override
      public Set<? extends I5ECharacterClass> getCharacterClasses() {
        return null;
      }
    };
  }
}
