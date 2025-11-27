// API module - contains API interfaces and DTOs

plugins {
    kotlin("plugin.spring")
}

dependencies {
    // Jackson Kotlin module for JSON serialization/deserialization in tests
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
