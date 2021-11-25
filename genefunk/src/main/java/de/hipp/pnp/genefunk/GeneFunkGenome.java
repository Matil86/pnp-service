package de.hipp.pnp.genefunk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hipp.pnp.Feature5e;
import de.hipp.pnp.interfaces.I5ECharacterRace;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("JpaAttributeTypeInspection")
@Entity
@Data
class GeneFunkGenome implements I5ECharacterRace {

    @Enumerated(EnumType.ORDINAL)
    GeneFunkGenomeType genomeType = GeneFunkGenomeType.Engineered;

    @Id
    String name;

    String description;

    @JsonIgnore
    HashMap<String, Integer> attributes = new HashMap<>();

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    List<Feature5e> features = new ArrayList<>();

    public void addAttributeChange(String key, Integer value) {
        attributes.put(key, value);
    }

    public void addFeature(String nameKey, String valueKey) {
        features.add(new Feature5e(nameKey, valueKey));
    }
}
