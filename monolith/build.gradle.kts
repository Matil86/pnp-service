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
    mainClass.set("de.hipp.pnp.CharacterGeneratorApplication")
    archiveClassifier.set("spring-boot")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    builder.set("paketobuildpacks/builder-jammy-tiny:latest")
    environment.set(mapOf(
        "BP_JVM_VERSION" to "24"
    ))
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

    // Observability: Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Observability: Distributed Tracing
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // Observability: Structured Logging
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")

    // Observability: AOP for Performance Monitoring
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Testing
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.7.2")
    testImplementation("io.mockk:mockk-jvm:1.13.8")
}
