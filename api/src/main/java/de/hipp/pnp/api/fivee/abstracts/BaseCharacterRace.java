package de.hipp.pnp.api.fivee.abstracts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class BaseCharacterRace {
  private String name;
  private String description;
  private HashMap<String, Integer> attributes = new HashMap<>();

  @JsonIgnore
  public void addAttributeChange(String key, Integer value){
    attributes.put(key, value);
  }
}
