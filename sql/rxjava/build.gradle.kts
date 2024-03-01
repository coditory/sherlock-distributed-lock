plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiRxjava)
    api(libs.r2dbc.spi)

    // implementation
    implementation(projects.common)
    implementation(projects.sql.sqlCommon)

    // integration
    integrationTestImplementation(projects.tests)
    integrationTestImplementation(libs.r2dbc.pool)
    // integration: postgres
    integrationTestImplementation(libs.postgresql)
    integrationTestImplementation(libs.r2dbc.postgresql)
    integrationTestImplementation(libs.testcontainers.postgresql)
    // integration: mysql
    integrationTestImplementation(libs.mysql)
    integrationTestImplementation(libs.r2dbc.mysql)
    integrationTestImplementation(libs.testcontainers.mysql)
}
