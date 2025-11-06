package de.hipp.pnp.genefunk

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration properties for character names.
 *
 * Loads the list of available character names from application properties.
 */
@Configuration
@ConfigurationProperties(prefix = "character")
class CharacterNamesProperties {
    /**
     * List of available character names for random generation.
     */
    var names: List<String> = emptyList()
}
