plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCoroutines)
    api(libs.r2dbc.spi)
    api(projects.api.apiCoroutinesConnector)
    implementation(projects.connectors.sql.sqlCommon)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    integrationTestImplementation(projects.connectors.sql.sqlTests)
    integrationTestImplementation(libs.r2dbc.pool)
    // integration: postgres
    integrationTestImplementation(libs.r2dbc.postgresql)
    integrationTestImplementation(libs.testcontainers.postgresql)
    // integration: mysql
    integrationTestImplementation(libs.r2dbc.mysql)
    integrationTestImplementation(libs.testcontainers.mysql)
}
