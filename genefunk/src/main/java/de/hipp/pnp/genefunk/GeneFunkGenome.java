package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hipp.pnp.api.Feature5e;
import de.hipp.pnp.interfaces.I5ECharacterRace;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("JpaAttributeTypeInspection")
@Entity
@Data
class GeneFunkGenome implements I5ECharacterRace {

    @Enumerated(EnumType.ORDINAL)
    GeneFunkGenomeType genomeType = GeneFunkGenomeType.ENGINEERED;

    @Id
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @JsonIgnore
    HashMap<String, Integer> attributes = new HashMap<>();

    @ElementCollection(targetClass = Feature5e.class, fetch = FetchType.EAGER)
    Set<Feature5e> features = new HashSet<>();

    public void addAttributeChange(String key, Integer value) {
        attributes.put(key, value);
    }

    public void addFeature(String nameKey, String valueKey) {
        features.add(new Feature5e(nameKey, valueKey));
    }
}
