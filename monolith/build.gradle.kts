plugins {
    id("org.springframework.boot")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
}

allOpen {
    annotation("org.springframework.boot.autoconfigure.SpringBootApplication")
    annotation("org.springframework.context.annotation.Configuration")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("de.hipp.pnp.CharacterGeneratorApplicationKt")
    archiveClassifier.set("spring-boot")
}

// Disable the plain JAR task - we only want the Spring Boot JAR
tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    builder.set("paketobuildpacks/builder-jammy-tiny:latest")
    environment.set(
        mapOf(
            // Note: Keeping Java 24 until Kotlin 2.3+ adds JVM 25 support
            "BP_JVM_VERSION" to "24",
        ),
    )
}

// Configure resource filtering to replace @<value>@ placeholders in YAML files
tasks.named<ProcessResources>("processResources") {
    // Capture project properties in configuration phase for configuration cache compatibility
    val projectVersion = project.version.toString()
    val sourceEncoding = "UTF-8"
    val javaVersion = JavaVersion.VERSION_25.toString()

    // Use filter to replace @<value>@ placeholders without treating the file as a Groovy template
    // This approach only replaces the specific @<value>@ patterns and leaves ${...} alone
    filesMatching("**/application.yaml") {
        filter { line ->
            line
                .replace("@project.version@", projectVersion)
                .replace("@project.build.sourceEncoding@", sourceEncoding)
                .replace("@java.version@", javaVersion)
        }
    }
    filesMatching("**/application.yml") {
        filter { line ->
            line
                .replace("@project.version@", projectVersion)
                .replace("@project.build.sourceEncoding@", sourceEncoding)
                .replace("@java.version@", javaVersion)
        }
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":base"))
    implementation(project(":data"))
    implementation(project(":genefunk"))
    implementation(project(":security"))
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.springframework.amqp:spring-rabbit")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework:spring-webflux")

    // Spring Boot 4.0: Modularized health and metrics into separate artifacts
    implementation("org.springframework.boot:spring-boot-health")
    implementation("org.springframework.boot:spring-boot-micrometer-metrics")

    // Observability: Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Observability: Distributed Tracing
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // Observability: Structured Logging
    implementation("net.logstash.logback:logstash-logback-encoder:9.0")

    // Observability: AOP for Performance Monitoring
    // Spring Boot 4.0: Renamed from spring-boot-starter-aop to spring-boot-starter-aspectj
    implementation("org.springframework.boot:spring-boot-starter-aspectj")

    // Testing
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.7.2")
    testImplementation("io.mockk:mockk-jvm:1.13.8")
}
