package de.hipp.pnp

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.Yaml

/**
 * Monolith Startup Test - Verifies application configuration and placeholder resolution.
 *
 * This test suite validates:
 * 1. YAML configuration files are properly processed
 * 2. @<value>@ placeholders are replaced with actual values during build
 * 3. Application configuration properties are correctly loaded
 * 4. Basic Spring Boot application structure is sound
 *
 * Test Strategy:
 * - Tests configuration files directly without starting Spring context
 * - Focuses on configuration validation rather than full integration
 * - Verifies build-time resource filtering worked correctly
 */
class MonolithStartupTest :
    StringSpec({

        "Jarvis - Application YAML file should exist" {
            val resource = ClassPathResource("application.yaml")

            resource.exists() shouldBe true
            resource.isReadable shouldBe true
        }

        "Jarvis - Application YAML should have placeholders replaced" {
            val resource = ClassPathResource("application.yaml")
            val content = resource.inputStream.bufferedReader().use { it.readText() }

            // Verify placeholders were replaced (should not contain @<value>@ format)
            content.contains("@project.version@") shouldBe false
            content.contains("@project.build.sourceEncoding@") shouldBe false
            content.contains("@java.version@") shouldBe false

            // Verify actual values are present
            content.contains("version:") shouldBe true
            content.contains("encoding:") shouldBe true
        }

        "Jarvis - Application YAML should contain required configuration" {
            val resource = ClassPathResource("application.yaml")
            val yaml = Yaml()
            val config = yaml.load<Map<String, Any>>(resource.inputStream)

            config shouldNotBe null

            // Verify spring configuration
            val spring = config["spring"] as? Map<*, *>
            spring shouldNotBe null
            spring?.get("application") shouldNotBe null

            // Verify management configuration
            val management = config["management"] as? Map<*, *>
            management shouldNotBe null

            // Verify info section
            val info = config["info"] as? Map<*, *>
            info shouldNotBe null
        }

        "Jarvis - Info properties should have values replaced" {
            val resource = ClassPathResource("application.yaml")
            val yaml = Yaml()
            val config = yaml.load<Map<String, Any>>(resource.inputStream)

            val info = config["info"] as? Map<*, *>
            info shouldNotBe null

            val app = info?.get("app") as? Map<*, *>
            app shouldNotBe null

            // Check version was replaced
            val version = app?.get("version")
            version shouldNotBe null
            version shouldNotBe "@project.version@"
            version.toString().contains("@") shouldBe false

            // Check encoding was replaced
            val encoding = app?.get("encoding")
            encoding shouldNotBe null
            encoding shouldBe "UTF-8"

            // Check java version was replaced
            val java = app?.get("java") as? Map<*, *>
            java shouldNotBe null
            val javaVersion = java?.get("version")
            javaVersion shouldNotBe null
            javaVersion shouldNotBe "@java.version@"
            javaVersion.toString().contains("@") shouldBe false
        }

        "Jarvis - Application name should be configured" {
            val resource = ClassPathResource("application.yaml")
            val yaml = Yaml()
            val config = yaml.load<Map<String, Any>>(resource.inputStream)

            val spring = config["spring"] as? Map<*, *>
            spring shouldNotBe null

            val application = spring?.get("application") as? Map<*, *>
            application shouldNotBe null

            val name = application?.get("name")
            name shouldNotBe null
            name shouldBe "pnp-character-generator"
        }

        "Jarvis - Management endpoints should be configured" {
            val resource = ClassPathResource("application.yaml")
            val yaml = Yaml()
            val config = yaml.load<Map<String, Any>>(resource.inputStream)

            val management = config["management"] as? Map<*, *>
            management shouldNotBe null

            val endpoints = management?.get("endpoints") as? Map<*, *>
            endpoints shouldNotBe null

            val web = endpoints?.get("web") as? Map<*, *>
            web shouldNotBe null

            val basePath = web?.get("base-path")
            basePath shouldBe "/actuator"
        }
    })
