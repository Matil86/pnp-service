package de.hipp.pnp.data.language;

import de.hipp.pnp.data.MultipleYamlPropertySourceFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@ConfigurationProperties
@PropertySources({
		@PropertySource(value = "classpath:genefunk/crb/languagekeys.yaml", factory = MultipleYamlPropertySourceFactory.class)
})
public class LanguageKeyConfiguration {

	private Map<String, Map<String, Map<String, LanguageKey>>> locale = new HashMap<>();

	@PostConstruct
	public void init() {
		log.info("LanguageKeyConfiguration initialized");
		log.info("LanguageKeyConfiguration: " + locale);
	}
}
