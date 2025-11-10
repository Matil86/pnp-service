package de.hipp.pnp

import de.hipp.data.config.LocalizationProperties
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ImportRuntimeHints

/**
 * Main application class for the PnP Character Generator service.
 *
 * This Spring Boot application provides character generation services for various
 * tabletop RPG systems including GeneFunk and 5e-based games.
 */
@SpringBootApplication(scanBasePackages = ["de.hipp.*"], proxyBeanMethods = false)
@EnableConfigurationProperties(LocalizationProperties::class)
@ImportRuntimeHints(CharacterGeneratorRuntimeHints::class)
class CharacterGeneratorApplication

fun main(args: Array<String>) {
    SpringApplication.run(CharacterGeneratorApplication::class.java, *args)
}

/**
 * Runtime hints registrar for GraalVM native image compilation.
 *
 * Registers necessary reflection hints for the application classes.
 */
class CharacterGeneratorRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(
        hints: RuntimeHints,
        classLoader: ClassLoader?,
    ) {
        // Register the application class for reflection
        hints.reflection().registerType(CharacterGeneratorApplication::class.java)
    }
}
