package de.hipp.pnp.security

import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportRuntimeHints

@SpringBootApplication(scanBasePackages = ["de.hipp.*"], proxyBeanMethods = false)
@ImportRuntimeHints(SecurityServiceRuntimeHints::class)
open class SecurityServiceApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SecurityServiceApplication::class.java, *args)
        }
    }
}

class SecurityServiceRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(
        hints: RuntimeHints,
        classLoader: ClassLoader?,
    ) {
        // Register the companion class for reflection
        hints.reflection().registerType(SecurityServiceApplication.Companion::class.java)
    }
}
