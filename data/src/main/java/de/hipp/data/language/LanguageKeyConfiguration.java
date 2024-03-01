package de.hipp.data.language;

import de.hipp.data.factory.YamlPropertySourceFactory;
import de.hipp.pnp.api.fivee.LanguageValue;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "languagekeys")
@PropertySource(value = "classpath:application-genefunk-crb-locale.yaml", factory = YamlPropertySourceFactory.class)
public class LanguageKeyConfiguration {
    // locale , game , book , languageKey,
    private Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> locale = new HashMap<>();
}
