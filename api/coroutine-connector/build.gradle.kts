description = "Sherlock Distributed Lock with Kotlin Coroutine Connector API"

plugins {
    kotlin("jvm") version "1.8.0"
    // id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-common"))
    api(project(":api:api-coroutine"))

    // implementation
    implementation(project(":common"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${versions["coroutines"]}"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    // test
    testImplementation(project(":inmem:inmem-coroutine"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions["junitVersion"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versions["junitVersion"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // integration
    integrationImplementation(project(":tests"))
}
