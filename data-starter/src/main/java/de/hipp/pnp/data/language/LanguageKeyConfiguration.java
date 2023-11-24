package de.hipp.pnp.data.language;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "locale")
public class LanguageKeyConfiguration {
	private Map<String, Map<String, LanguageKey>> enUS = new HashMap<>();

}
