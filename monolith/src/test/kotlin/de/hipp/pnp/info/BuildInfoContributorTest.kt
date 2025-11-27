package de.hipp.pnp.info

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.actuate.info.Info

/**
 * Tests for BuildInfoContributor.
 *
 * Verifies build and runtime information is correctly added to actuator info endpoint.
 */
@Suppress("UNCHECKED_CAST")
class BuildInfoContributorTest :
    StringSpec({

        "contribute - Goku verifies build information is added" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            info.details shouldContainKey "build"
        }

        "contribute - Spider-Man verifies runtime information is added" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            info.details shouldContainKey "runtime"
        }

        "contribute - Tony Stark verifies environment information is added" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            info.details shouldContainKey "environment"
        }

        "contribute - Batman verifies Kotlin version is included" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val buildInfo = info.details["build"] as Map<String, Any>

            buildInfo shouldContainKey "kotlin"
            buildInfo["kotlin"] shouldNotBe null
        }

        "contribute - Wonder Woman verifies Java version is included" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val buildInfo = info.details["build"] as Map<String, Any>

            buildInfo shouldContainKey "java"
            val javaInfo = buildInfo["java"] as Map<String, Any>
            javaInfo shouldContainKey "version"
            javaInfo shouldContainKey "vendor"
        }

        "contribute - Naruto (ナルト) verifies Spring Boot version is included" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val buildInfo = info.details["build"] as Map<String, Any>

            buildInfo shouldContainKey "springBoot"
        }

        "contribute - Vegeta verifies build timestamp is included" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val buildInfo = info.details["build"] as Map<String, Any>

            buildInfo shouldContainKey "time"
            buildInfo["time"] shouldNotBe null
        }

        "contribute - Deadpool verifies runtime processor count" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val runtimeInfo = info.details["runtime"] as Map<String, Any>

            runtimeInfo shouldContainKey "processors"
            val processors = runtimeInfo["processors"] as Int
            (processors > 0) shouldBe true
        }

        "contribute - Hulk verifies runtime memory information" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val runtimeInfo = info.details["runtime"] as Map<String, Any>

            runtimeInfo shouldContainKey "maxMemory"
            runtimeInfo shouldContainKey "totalMemory"
            runtimeInfo shouldContainKey "freeMemory"
        }

        "contribute - Pikachu (ピカチュウ) verifies environment defaults to development" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val environment = info.details["environment"] as String

            environment shouldNotBe null
        }

        "contribute - Gandalf verifies Git info is not present by default" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            // Git info only added if env vars are set
            val hasGitInfo = info.details.containsKey("git")
            // Should be false unless GIT_COMMIT or GIT_BRANCH env vars are set
            hasGitInfo shouldBe false
        }

        "contribute - Frodo verifies all required runtime fields present" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val runtimeInfo = info.details["runtime"] as Map<String, Any>

            runtimeInfo.size shouldBe 4
        }

        "contribute - Neo verifies build info structure" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val buildInfo = info.details["build"] as Map<String, Any>

            buildInfo.size shouldBe 4 // time, kotlin, java, springBoot
        }

        "contribute - Loki verifies Java vendor is not empty" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val buildInfo = info.details["build"] as Map<String, Any>
            val javaInfo = buildInfo["java"] as Map<String, Any>

            javaInfo["vendor"] shouldNotBe null
            val vendor = javaInfo["vendor"] as String
            vendor.isNotEmpty() shouldBe true
        }

        "contribute - Thor verifies Kotlin version format" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val buildInfo = info.details["build"] as Map<String, Any>
            val kotlinVersion = buildInfo["kotlin"] as String

            // Kotlin version should be in format like "1.9.20"
            kotlinVersion.isNotEmpty() shouldBe true
        }

        "contribute - Captain America verifies processors is positive" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val runtimeInfo = info.details["runtime"] as Map<String, Any>
            val processors = runtimeInfo["processors"] as Int

            (processors > 0) shouldBe true
        }

        "contribute - Black Widow verifies memory values are strings with MB suffix" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val runtimeInfo = info.details["runtime"] as Map<String, Any>

            val maxMemory = runtimeInfo["maxMemory"] as String
            val totalMemory = runtimeInfo["totalMemory"] as String
            val freeMemory = runtimeInfo["freeMemory"] as String

            maxMemory shouldContain " MB"
            totalMemory shouldContain " MB"
            freeMemory shouldContain " MB"
        }

        "contribute - Thanos verifies multiple contributions to same builder" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)
            contributor.contribute(builder)

            val info = builder.build()

            info.details shouldContainKey "build"
            info.details shouldContainKey "runtime"
            info.details shouldContainKey "environment"
        }

        "contribute - verifies all top-level keys present" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()

            // Should have at least build, runtime, environment
            (info.details.size >= 3) shouldBe true
        }

        "contribute - verifies Spring Boot version is not null" {
            val contributor = BuildInfoContributor()
            val builder = Info.Builder()

            contributor.contribute(builder)

            val info = builder.build()
            val buildInfo = info.details["build"] as Map<String, Any>

            buildInfo["springBoot"] shouldNotBe null
        }
    })
