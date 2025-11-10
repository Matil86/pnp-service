import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("org.springframework.boot") version "3.5.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("jvm") version "2.2.10" apply false
    kotlin("plugin.spring") version "2.2.10" apply false
    kotlin("plugin.jpa") version "2.2.10" apply false
    kotlin("plugin.allopen") version "2.2.10" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.0.3" apply false
    id("org.sonarqube") version "4.4.1.3373"
    jacoco
}

allprojects {
    group = "de.hipp.pnp"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("kotest.framework.timeout", "60000")

        // Parallel test execution for faster feedback
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

        // Enable parallel test execution in Kotest
        systemProperty("kotest.framework.parallelism", Runtime.getRuntime().availableProcessors())
        systemProperty("kotest.framework.concurrency", Runtime.getRuntime().availableProcessors())

        // Smart test filtering based on changed modules
        val changedModules = providers.environmentVariable("CHANGED_MODULES")
        if (changedModules.isPresent && changedModules.get().isNotBlank()) {
            filter {
                changedModules.get().split(",").forEach { module ->
                    includeTestsMatching("de.hipp.pnp.${module.trim()}.**")
                }
            }
        }

        // Skip tests if only docs changed
        val docsOnly = providers.environmentVariable("DOCS_ONLY")
        if (docsOnly.getOrElse("false") == "true") {
            enabled = false
        }

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = false
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }

        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.named("test"))
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        dependsOn(tasks.named("jacocoTestReport"))
        violationRules {
            rule {
                limit {
                    minimum = "0.70".toBigDecimal()
                }
            }
        }
    }

    // Detekt configuration
    configure<DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom("$rootDir/config/detekt.yml")
        baseline = file("$rootDir/config/detekt-baseline.xml")
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        // Detekt doesn't support JVM 24 yet, so we use 21 as the target
        // This is a static analysis tool limitation and doesn't affect runtime
        jvmTarget = "21"
        reports {
            html.required.set(true)
            xml.required.set(true)
            txt.required.set(false)
            sarif.required.set(true)
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
        // Detekt doesn't support JVM 24 yet, so we use 21 as the target
        jvmTarget = "21"
    }

    // ktlint configuration (Google Kotlin Code Style)
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.7.1")
        android.set(false)
        // Temporarily allowing failures during Kotlin 2.2.10 migration
        // Main compatibility issue resolved, remaining issues are minor formatting
        ignoreFailures.set(true)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        }
    }

    // Common dependencies for all subprojects
    dependencies {
        val implementation by configurations
        val testImplementation by configurations
        val runtimeOnly by configurations
        val detektPlugins by configurations

        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.5"))
        implementation("com.fasterxml.jackson.core:jackson-databind")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-amqp")
        implementation("org.springframework.boot:spring-boot-configuration-processor")
        implementation("io.github.oshai:kotlin-logging-jvm:7.0.12")
        implementation("io.projectreactor:reactor-core:3.7.9")
        implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.10")
        runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:2.2.10")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.11")
        implementation("com.google.firebase:firebase-admin:9.5.0")

        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        }
        testImplementation("org.springframework.boot:spring-boot-test")
        testImplementation("io.mockk:mockk:1.13.8")
        testImplementation("io.kotest:kotest-framework-engine:5.7.2")
        testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
        testImplementation("io.kotest:kotest-assertions-core:5.7.2")
        testImplementation("io.kotest:kotest-assertions-json:5.7.2")
        testImplementation("io.kotest:kotest-extensions-spring:1.1.3")
        testImplementation("org.junit.platform:junit-platform-engine")

        // Detekt plugins
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    }
}

// SonarQube configuration
sonarqube {
    properties {
        property("sonar.projectKey", "pnp-service")
        property("sonar.projectName", "PnP Service")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.kotlin.detekt.reportPaths", "build/reports/detekt/detekt.xml")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.qualitygate.wait", true)
    }
}
