package de.hipp.pnp;

import de.hipp.pnp.data.language.LanguageKeyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.hipp.*")
public class DataServiceApplication {

	@Autowired
	LanguageKeyConfiguration config;

	public static void main(String[] args) {
		SpringApplication.run(DataServiceApplication.class, args);

	}
}
