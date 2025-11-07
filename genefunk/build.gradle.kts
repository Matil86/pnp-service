plugins {
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    id("me.champeau.jmh") version "0.7.2"
}

jmh {
    jmhVersion = "1.37"
    includeTests = false
    duplicateClassesStrategy = DuplicatesStrategy.WARN
}

dependencies {
    implementation(project(":api"))
    implementation(project(":base"))
    implementation("org.springframework.amqp:spring-rabbit")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.7.2")
    testImplementation("io.mockk:mockk-jvm:1.13.8")
}
