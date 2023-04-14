package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.fivee.abstracts.BaseCharacterRace;
import de.hipp.pnp.base.fivee.Feature5e;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

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

	@ElementCollection(targetClass = Feature5e.class, fetch = FetchType.EAGER)
	Set<Feature5e> features = new HashSet<>();

	public void addFeature(String nameKey, String valueKey) {
		features.add(new Feature5e(nameKey, valueKey));
	}
}
