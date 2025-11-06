package de.hipp.pnp.info

import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Custom info contributor for application build and runtime information.
 *
 * Adds detailed build metadata to the /actuator/info endpoint,
 * including Kotlin version, build time, and runtime information.
 */
@Component
class BuildInfoContributor : InfoContributor {

    companion object {
        private val BUILD_TIME = Instant.now()
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
            .withZone(ZoneId.of("UTC"))
    }

    override fun contribute(builder: Info.Builder) {
        // Add Kotlin version
        val kotlinVersion = KotlinVersion.CURRENT.toString()

        // Add Java version
        val javaVersion = System.getProperty("java.version")
        val javaVendor = System.getProperty("java.vendor")

        // Add Spring Boot version (from build info if available)
        val springBootVersion = org.springframework.boot.SpringBootVersion.getVersion()

        // Add build timestamp
        val buildTimestamp = DATE_FORMATTER.format(BUILD_TIME)

        // Add runtime information
        val runtime = mapOf(
            "processors" to Runtime.getRuntime().availableProcessors(),
            "maxMemory" to "${Runtime.getRuntime().maxMemory() / 1024 / 1024} MB",
            "totalMemory" to "${Runtime.getRuntime().totalMemory() / 1024 / 1024} MB",
            "freeMemory" to "${Runtime.getRuntime().freeMemory() / 1024 / 1024} MB"
        )

        // Add all information to the info endpoint
        builder.withDetail("build", mapOf(
            "time" to buildTimestamp,
            "kotlin" to kotlinVersion,
            "java" to mapOf(
                "version" to javaVersion,
                "vendor" to javaVendor
            ),
            "springBoot" to springBootVersion
        ))

        builder.withDetail("runtime", runtime)

        // Add environment information
        val environment = System.getenv("ENVIRONMENT") ?: "development"
        builder.withDetail("environment", environment)

        // Add Git info if available (this would be populated by git-commit-id-plugin)
        val gitCommit = System.getenv("GIT_COMMIT") ?: "unknown"
        val gitBranch = System.getenv("GIT_BRANCH") ?: "unknown"

        if (gitCommit != "unknown" || gitBranch != "unknown") {
            builder.withDetail("git", mapOf(
                "commit" to gitCommit,
                "branch" to gitBranch
            ))
        }
    }
}
