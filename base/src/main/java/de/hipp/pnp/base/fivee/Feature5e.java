package de.hipp.pnp.base.fivee;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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
