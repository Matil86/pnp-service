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
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    builder.set("paketobuildpacks/builder-jammy-tiny:latest")
    environment.set(mapOf(
        "BP_NATIVE_IMAGE" to "true",
        "BP_JVM_VERSION" to "24"
    ))
}

dependencies {
    implementation(project(":api"))
    implementation(project(":base"))
    implementation(project(":data"))
    implementation(project(":genefunk"))
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.springframework.amqp:spring-rabbit")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework:spring-webflux")
}
