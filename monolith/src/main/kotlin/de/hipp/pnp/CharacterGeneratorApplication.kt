package de.hipp.pnp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.MemberCategory

@SpringBootApplication(scanBasePackages = ["de.hipp.*"], proxyBeanMethods = false)
@ImportRuntimeHints(CharacterGeneratorRuntimeHints::class)
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
        // Register main class with all member categories for complete reflection access
        hints.reflection()
            .registerType(
                CharacterGeneratorApplication::class.java,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS
            )
        
        // Register companion class with all member categories
        hints.reflection()
            .registerType(
                CharacterGeneratorApplication.Companion::class.java,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS
            )
            
        // Register Spring resources that might be needed
        hints.resources()
            .registerPattern("META-INF/spring.*")
            .registerPattern("META-INF/spring.factories")
    }
}
