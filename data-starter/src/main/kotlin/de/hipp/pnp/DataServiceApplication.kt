package de.hipp.pnp

import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportRuntimeHints

@SpringBootApplication(scanBasePackages = ["de.hipp.*"], proxyBeanMethods = false)
@ImportRuntimeHints(DataServiceRuntimeHints::class)
open class DataServiceApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DataServiceApplication::class.java, *args)
        }
    }
}

class DataServiceRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(
        hints: RuntimeHints,
        classLoader: ClassLoader?,
    ) {
        // Register the companion class for reflection
        hints.reflection().registerType(DataServiceApplication.Companion::class.java)
    }
}
