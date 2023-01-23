description = "Sherlock Distributed Lock in-memory coroutine implementation"

plugins {
    kotlin("jvm") version "1.8.0"
    // id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-coroutine"))
    api(project(":inmem:inmem-common"))

    // implementation
    implementation(project(":api:api-coroutine-connector"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${versions["coroutines"]}"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

    // tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions["junit"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versions["junit"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // integration
    integrationImplementation(project(":tests"))
}
