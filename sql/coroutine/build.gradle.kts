plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCoroutine)
    api(libs.r2dbc.spi)
    api(projects.api.apiCoroutineConnector)
    implementation(projects.sql.sqlCommon)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    // integration
    integrationTestImplementation(projects.sql.sqlCommonTests)
    integrationTestImplementation(libs.r2dbc.pool)
    // integration: postgres
    integrationTestImplementation(libs.r2dbc.postgresql)
    integrationTestImplementation(libs.testcontainers.postgresql)
    // integration: mysql
    integrationTestImplementation(libs.r2dbc.mysql)
    integrationTestImplementation(libs.testcontainers.mysql)
}
