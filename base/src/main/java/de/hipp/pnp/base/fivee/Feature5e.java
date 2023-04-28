package de.hipp.pnp.base.fivee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Feature5e {

	@Id
	@GeneratedValue
	private Long id;

	@Column(columnDefinition = "TEXT")
	String label;

	@Column(columnDefinition = "TEXT")
	String description;

	int availableAtLevel = 0;

	public Feature5e(String label, String description) {
		this(label, description, 0);
	}

	public Feature5e(String label, String description, int availableAtLevel) {

		this.label = label;
		this.description = description;
		this.availableAtLevel = availableAtLevel;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
