package de.hipp.pnp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

@SpringBootApplication(scanBasePackages = ["de.hipp.*"], proxyBeanMethods = false)
@ImportRuntimeHints(GeneFunkServiceRuntimeHints::class)
open class GeneFunkServiceApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GeneFunkServiceApplication::class.java, *args)
        }
    }
}

class GeneFunkServiceRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        // Register the companion class for reflection
        hints.reflection().registerType(GeneFunkServiceApplication.Companion::class.java)
    }
}
