description = "Sherlock Distributed Lock implementation for coroutine using mongo reactive driver"

plugins {
    kotlin("jvm") version "1.8.0"
    // id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-coroutine"))
    api("org.mongodb:mongodb-driver-reactivestreams:${versions["mongodbReactive"]}")

    // implementation
    implementation(project(":api:api-coroutine-connector"))
    implementation(project(":mongo:mongo-common"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${versions["coroutines"]}"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions["junit"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versions["junit"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // integration
    integrationImplementation(project(":tests"))
    integrationImplementation("org.testcontainers:mongodb:${versions["testContainers"]}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
}