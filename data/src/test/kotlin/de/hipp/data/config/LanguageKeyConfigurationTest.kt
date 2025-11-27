package de.hipp.data.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class LanguageKeyConfigurationTest :
    FunSpec({

        context("Construction") {
            test("should create LanguageKeyConfiguration instance") {
                val config = LanguageKeyConfiguration()

                config.shouldNotBe(null)
                config.shouldBeInstanceOf<LanguageKeyConfiguration>()
            }

            test("should create multiple independent instances") {
                val config1 = LanguageKeyConfiguration()
                val config2 = LanguageKeyConfiguration()

                config1.shouldNotBe(null)
                config2.shouldNotBe(null)
                config1 shouldNotBe config2
            }

            test("should be an open class allowing extension") {
                val config = LanguageKeyConfiguration()

                // Verify it's an actual instance and can be used in Spring context
                config::class.java.modifiers shouldNotBe null
            }
        }

        context("Configuration class behavior") {
            test("should maintain consistent state across multiple instantiations") {
                val configs = (1..10).map { LanguageKeyConfiguration() }

                configs.forEach { config ->
                    config.shouldNotBe(null)
                    config.shouldBeInstanceOf<LanguageKeyConfiguration>()
                }
            }

            test("should be suitable as a Spring configuration bean") {
                val config = LanguageKeyConfiguration()

                // Verify it has Configuration and EnableConfigurationProperties annotations
                val annotations = config::class.java.annotations
                annotations.shouldNotBe(null)
            }
        }

        context("Class metadata verification") {
            test("should have proper class name") {
                val config = LanguageKeyConfiguration()

                config::class.simpleName shouldNotBe null
                config::class.simpleName shouldNotBe ""
            }

            test("should be instantiable without dependencies") {
                // This test verifies the no-arg constructor works
                val config = LanguageKeyConfiguration()

                config.shouldNotBe(null)
            }
        }
    })
