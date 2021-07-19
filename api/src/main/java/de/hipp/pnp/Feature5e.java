package de.hipp.pnp;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Feature5e {

    @Id
    String label;
    String description;
    int availableAtLevel = 0;

    public Feature5e(String label, String description) {
        this.label = label;
        this.description = description;
    }
}
