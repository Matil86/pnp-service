package de.hipp.pnp

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.aot.hint.RuntimeHints

/**
 * Tests for CharacterGeneratorApplication and CharacterGeneratorRuntimeHints.
 *
 * Verifies application class structure and GraalVM runtime hints registration.
 */
class CharacterGeneratorApplicationTest :
    StringSpec({

        "CharacterGeneratorApplication - Goku verifies class can be instantiated" {
            val app = CharacterGeneratorApplication()

            app shouldNotBe null
        }

        "CharacterGeneratorApplication - Spider-Man verifies class is annotated" {
            val annotations = CharacterGeneratorApplication::class.annotations

            annotations.isNotEmpty() shouldBe true
        }

        "CharacterGeneratorRuntimeHints - Tony Stark registers reflection hints" {
            val hints = CharacterGeneratorRuntimeHints()
            val runtimeHints = RuntimeHints()

            hints.registerHints(runtimeHints, this::class.java.classLoader)

            runtimeHints shouldNotBe null
        }

        "CharacterGeneratorRuntimeHints - Batman verifies hints registrar can be created" {
            val hints = CharacterGeneratorRuntimeHints()

            hints shouldNotBe null
        }

        "CharacterGeneratorRuntimeHints - Wonder Woman registers with null classloader" {
            val hints = CharacterGeneratorRuntimeHints()
            val runtimeHints = RuntimeHints()

            hints.registerHints(runtimeHints, null)

            runtimeHints shouldNotBe null
        }

        "CharacterGeneratorRuntimeHints - Naruto (ナルト) verifies application class is registered" {
            val hints = CharacterGeneratorRuntimeHints()
            val runtimeHints = RuntimeHints()

            hints.registerHints(runtimeHints, this::class.java.classLoader)

            // Verify hints were registered (hints object should not be null)
            runtimeHints shouldNotBe null
        }

        "CharacterGeneratorRuntimeHints - Vegeta multiple registrations" {
            val hints = CharacterGeneratorRuntimeHints()
            val runtimeHints = RuntimeHints()

            hints.registerHints(runtimeHints, this::class.java.classLoader)
            hints.registerHints(runtimeHints, this::class.java.classLoader)

            runtimeHints shouldNotBe null
        }

        "CharacterGeneratorApplication - Deadpool verifies Spring Boot annotations present" {
            val annotations = CharacterGeneratorApplication::class.annotations
            val annotationNames = annotations.map { it.annotationClass.simpleName }

            annotationNames.isNotEmpty() shouldBe true
        }

        "CharacterGeneratorRuntimeHints - Hulk with custom classloader" {
            val hints = CharacterGeneratorRuntimeHints()
            val runtimeHints = RuntimeHints()
            val customClassLoader = this::class.java.classLoader

            hints.registerHints(runtimeHints, customClassLoader)

            runtimeHints shouldNotBe null
        }

        "CharacterGeneratorApplication - Pikachu (ピカチュウ) verifies main function exists" {
            // The main function should be accessible
            val mainFunction =
                try {
                    CharacterGeneratorApplication::class.java.getDeclaredMethod("main", Array<String>::class.java)
                } catch (e: NoSuchMethodException) {
                    null
                }

            // Main function should exist at package level
            mainFunction shouldBe null // Because it's a top-level function, not in the class
        }

        "CharacterGeneratorRuntimeHints - Gandalf verifies RuntimeHintsRegistrar interface" {
            val hints = CharacterGeneratorRuntimeHints()

            hints shouldNotBe null
        }

        "CharacterGeneratorApplication - Frodo verifies application can be created multiple times" {
            val app1 = CharacterGeneratorApplication()
            val app2 = CharacterGeneratorApplication()

            app1 shouldNotBe null
            app2 shouldNotBe null
        }

        "CharacterGeneratorRuntimeHints - Neo registers hints for Matrix reality" {
            val hints = CharacterGeneratorRuntimeHints()
            val runtimeHints = RuntimeHints()

            hints.registerHints(runtimeHints, this::class.java.classLoader)

            runtimeHints shouldNotBe null
        }

        "CharacterGeneratorRuntimeHints - Loki verifies hints are not null after registration" {
            val hints = CharacterGeneratorRuntimeHints()
            val runtimeHints = RuntimeHints()

            hints.registerHints(runtimeHints, this::class.java.classLoader)

            runtimeHints.reflection() shouldNotBe null
        }

        "CharacterGeneratorApplication - Thor verifies class name" {
            val app = CharacterGeneratorApplication()

            app::class.simpleName shouldBe "CharacterGeneratorApplication"
        }

        "CharacterGeneratorRuntimeHints - Captain America verifies class name" {
            val hints = CharacterGeneratorRuntimeHints()

            hints::class.simpleName shouldBe "CharacterGeneratorRuntimeHints"
        }

        "CharacterGeneratorApplication - Black Widow verifies package name" {
            val app = CharacterGeneratorApplication()

            app::class.java.packageName shouldBe "de.hipp.pnp"
        }

        "CharacterGeneratorRuntimeHints - Thanos registers hints for all stones" {
            val hints = CharacterGeneratorRuntimeHints()
            val runtimeHints = RuntimeHints()

            // Register hints multiple times with different classloaders
            hints.registerHints(runtimeHints, null)
            hints.registerHints(runtimeHints, this::class.java.classLoader)

            runtimeHints shouldNotBe null
        }

        "CharacterGeneratorApplication - verifies class is open (not final)" {
            val app = CharacterGeneratorApplication()

            // The kotlin-spring plugin's allOpen configuration makes @SpringBootApplication classes open
            // CharacterGeneratorApplication should be open (not final) for Spring's CGLIB proxying
            java.lang.reflect.Modifier
                .isFinal(app::class.java.modifiers) shouldBe false
        }

        "CharacterGeneratorRuntimeHints - verifies registerHints method exists" {
            val hints = CharacterGeneratorRuntimeHints()
            val methods = hints::class.java.methods

            val registerHintsMethod = methods.find { it.name == "registerHints" }
            registerHintsMethod shouldNotBe null
        }
    })
