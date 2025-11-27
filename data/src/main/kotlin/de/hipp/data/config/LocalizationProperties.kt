package de.hipp.data.config

import de.hipp.pnp.api.locale.SystemLocale
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties
data class LocalizationProperties(
    var systems: Map<String, SystemLocale> = emptyMap(),
)
