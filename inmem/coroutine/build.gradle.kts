description = "Sherlock Distributed Lock in-memory coroutine implementation"

plugins {
    kotlin("jvm") version "1.8.0"
    // id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

dependencies {
    api(project(":api:api-coroutine"))
    api(project(":inmem:inmem-common"))
    implementation(project(":api:api-coroutine-connector"))
    integrationImplementation(project(":tests"))

    // kotlin coroutines
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    // integrationImplementation project(':inmem:inmem-reactor')
    // integrationImplementation project(':inmem:inmem-common')
    integrationImplementation(project(":tests"))

    val junitVersion = "5.9.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}
