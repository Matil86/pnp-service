package de.hipp.data.config

import de.hipp.pnp.api.fivee.LanguageValue
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "languagekeys")
open class LanguageKeyConfiguration {
    // locale , game , book , languageKey,
    val locale: Map<String, Map<String, Map<String, Map<String, LanguageValue>>>> = mutableMapOf()
}
