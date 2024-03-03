plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiReactor)
    api(libs.r2dbc.spi)
    implementation(projects.sql.sqlCommon)
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
