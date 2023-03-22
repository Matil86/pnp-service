package de.hipp.pnp.api.fivee.abstracts;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseCharacter {

  private int gameType;
  private String firstName;
  private String lastName;
  private Integer level;
  private BaseCharacterRace race;
  protected Set<BaseCharacterClass> characterClasses;
}
