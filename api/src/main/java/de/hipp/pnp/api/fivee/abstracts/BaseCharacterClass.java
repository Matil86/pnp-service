package de.hipp.pnp.api.fivee.abstracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class BaseCharacterClass {

  private String name;
  private Integer level;

  public void increaseLevel(int amount){
    this.level = (this.level == null ? amount : this.level+amount);
  }
}
