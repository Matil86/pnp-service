package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.fivee.abstracts.BaseOrigin;
import de.hipp.pnp.base.fivee.Feature5e;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.Set;

@Entity
class GeneFunkGenome extends BaseOrigin {

    @Enumerated(EnumType.ORDINAL)
    GeneFunkGenomeType genomeType = GeneFunkGenomeType.ENGINEERED;

    @Id
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Nullable
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<Feature5e> features = new HashSet<>();

    public void addFeature(Feature5e feature5e) {
        features.add(feature5e);
    }

    public GeneFunkGenomeType getGenomeType() {
        return genomeType;
    }

    public void setGenomeType(GeneFunkGenomeType genomeType) {
        this.genomeType = genomeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public Set<Feature5e> getFeatures() {
        return features;
    }

    public void setFeatures(@Nullable Set<Feature5e> features) {
        this.features = features;
    }
}
