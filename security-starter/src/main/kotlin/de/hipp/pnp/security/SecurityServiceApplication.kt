package de.hipp.pnp.security

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

class SecurityServiceRuntimeHints : org.springframework.aot.hint.RuntimeHintsRegistrar {
    override fun registerHints(hints: org.springframework.aot.hint.RuntimeHints, classLoader: ClassLoader?) {
        // Register hints for AOT processing if needed
    }
}
