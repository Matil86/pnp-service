package de.hipp.pnp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@SpringBootApplication(scanBasePackages = ["de.hipp.*"], proxyBeanMethods = false)
@ImportRuntimeHints(CharacterGeneratorRuntimeHints::class)
@RegisterReflectionForBinding(CharacterGeneratorApplication.Companion::class)
open class CharacterGeneratorApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(CharacterGeneratorApplication::class.java, *args)
        }
    }

    // Empty constructor to ensure AOT processing works correctly
    constructor()
}

class CharacterGeneratorRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        // Register both the main class and companion class for reflection
        hints.reflection().registerType(CharacterGeneratorApplication::class.java)
        hints.reflection().registerType(CharacterGeneratorApplication.Companion::class.java)
    }
}
