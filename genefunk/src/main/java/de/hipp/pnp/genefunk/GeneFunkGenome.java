package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hipp.pnp.api.fivee.abstracts.BaseCharacterRace;
import de.hipp.pnp.base.fivee.Feature5e;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import lombok.Data;

@SuppressWarnings("JpaAttributeTypeInspection")
@Entity
@Data
class GeneFunkGenome extends BaseCharacterRace {

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

    @Override
    public void addAttributeChange(String key, Integer value) {
        attributes.put(key, value);
    }

    public void addFeature(String nameKey, String valueKey) {
        features.add(new Feature5e(nameKey, valueKey));
    }
}
