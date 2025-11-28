plugins {
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":base"))
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.google.firebase:firebase-admin")
    implementation("io.projectreactor:reactor-core")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.7.2")
    testImplementation("io.mockk:mockk-jvm:1.13.8")
    testImplementation("org.junit.platform:junit-platform-engine")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:2.3.0-RC")
}
