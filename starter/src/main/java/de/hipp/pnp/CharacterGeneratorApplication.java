package de.hipp.pnp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.hipp.*")
public class CharacterGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CharacterGeneratorApplication.class, args);
	}
}
