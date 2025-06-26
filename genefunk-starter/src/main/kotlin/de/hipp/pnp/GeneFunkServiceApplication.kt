package de.hipp.pnp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportRuntimeHints

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

class GeneFunkServiceRuntimeHints : org.springframework.aot.hint.RuntimeHintsRegistrar {
    override fun registerHints(hints: org.springframework.aot.hint.RuntimeHints, classLoader: ClassLoader?) {
        // Register hints for AOT processing if needed
    }
}
