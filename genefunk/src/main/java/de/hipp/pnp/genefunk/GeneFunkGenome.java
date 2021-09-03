package de.hipp.pnp.genefunk;

import de.hipp.pnp.Feature5e;
import de.hipp.pnp.interfaces.I5ECharacterRace;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@SuppressWarnings("JpaAttributeTypeInspection")
class GeneFunkGenome implements I5ECharacterRace {

    @Enumerated(EnumType.ORDINAL)
    GeneFunkGenomeType genomeType = GeneFunkGenomeType.Engineered;

    @Id
    String name;

    String description;

    Map<String, Object> attributes = new HashMap<>();

    @ManyToMany(cascade = CascadeType.ALL)
    List<Feature5e> features = new ArrayList<>();

    public void addAttributeChange(String key, Object value) {
        attributes.put(key, value);
    }

    public void addFeature(String nameKey, String valueKey) {
        features.add(new Feature5e(nameKey, valueKey));
    }
}
