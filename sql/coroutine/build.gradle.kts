description = "Sherlock Distributed Lock implementation for coroutine using SQL asynchronous connector R2DBC"

plugins {
    kotlin("jvm") version "1.8.0"
    // id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>
    api(project(":api:api-coroutine"))
    api("io.r2dbc:r2dbc-spi:${versions["r2dbc"]}")
    implementation(project(":api:api-coroutine-connector"))
    implementation(project(":common"))
    implementation(project(":sql:sql-common"))
    // kotlin coroutines
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

    // tests
    val junitVersion = "5.9.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // integration
    integrationImplementation(project(":tests"))
    integrationImplementation("io.r2dbc:r2dbc-pool:${versions["r2dbc"]}")
    // postgres
    integrationImplementation("org.postgresql:postgresql:${versions["postgresql"]}")
    integrationImplementation("org.postgresql:r2dbc-postgresql:${versions["r2dbc"]}")
    integrationImplementation("org.testcontainers:postgresql:${versions["testContainers"]}")
    // mysql
    integrationImplementation("mysql:mysql-connector-java:${versions["mysql"]}")
    // integrationImplementation("dev.miku:r2dbc-mysql:${versions["r2dbc"]}")
    integrationImplementation("org.testcontainers:mysql:${versions["testContainers"]}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
}