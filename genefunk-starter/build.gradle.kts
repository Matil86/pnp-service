plugins {
    id("org.springframework.boot")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("de.hipp.pnp.GeneFunkServiceApplication")
    archiveClassifier.set("spring-boot")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    builder.set("paketobuildpacks/builder-jammy-tiny:latest")
}

dependencies {
    implementation(project(":base"))
    implementation(project(":genefunk"))
}
