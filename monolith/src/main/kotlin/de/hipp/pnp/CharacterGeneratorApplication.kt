package de.hipp.pnp

import de.hipp.data.config.LocalizationProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

/**
 * Main application class for the PnP Character Generator service.
 *
 * This Spring Boot application provides character generation services for various
 * tabletop RPG systems including GeneFunk and 5e-based games.
 */
@SpringBootApplication(scanBasePackages = ["de.hipp.*"], proxyBeanMethods = false)
@EnableConfigurationProperties(LocalizationProperties::class)
class CharacterGeneratorApplication

fun main(args: Array<String>) {
    SpringApplication.run(CharacterGeneratorApplication::class.java, *args)
}
