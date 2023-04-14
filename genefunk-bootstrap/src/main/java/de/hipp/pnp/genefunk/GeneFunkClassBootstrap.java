package de.hipp.pnp.genefunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class GeneFunkClassBootstrap {

	private final GeneFunkClassRepository repository;

	GeneFunkClassBootstrap(GeneFunkClassRepository repository) {
		this.repository = repository;
		initialize();
	}

	protected void initialize() {
		if (this.repository.findByName("Biohacker") == null) {
			this.repository.save(this.initiateBiohacker());
		}
		if (this.repository.findByName("Gunfighter") == null) {
			this.repository.save(this.initiateGunfighter());
		}
	}


	private GeneFunkClass initiateBiohacker() {
		GeneFunkClass biohacker = new GeneFunkClass();
		biohacker.setName("Biohacker");
		return biohacker;
	}

	private GeneFunkClass initiateGunfighter() {
		GeneFunkClass biohacker = new GeneFunkClass();
		biohacker.setName("Gunfighter");
		return biohacker;
	}
}
