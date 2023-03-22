package de.hipp.pnp.base.fivee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Feature5e {

  @Id
  String label;
  @Column(columnDefinition = "TEXT")
  String description;
  int availableAtLevel = 0;

  public Feature5e(String label, String description) {
    this.label = label;
    this.description = description;
  }
}
