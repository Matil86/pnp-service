package de.hipp.pnp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportRuntimeHints

@SpringBootApplication(scanBasePackages = ["de.hipp.*"], proxyBeanMethods = false)
@ImportRuntimeHints(CharacterGeneratorRuntimeHints::class)
open class CharacterGeneratorApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(CharacterGeneratorApplication::class.java, *args)
        }
    }
}

class CharacterGeneratorRuntimeHints : org.springframework.aot.hint.RuntimeHintsRegistrar {
    override fun registerHints(hints: org.springframework.aot.hint.RuntimeHints, classLoader: ClassLoader?) {
        // Register hints for AOT processing if needed
    }
}
