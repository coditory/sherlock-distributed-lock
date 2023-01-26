plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiSync)

    // implementation
    implementation(projects.common)
    implementation(projects.sql.sqlCommon)

    // integration
    integrationTestImplementation(projects.tests)
    integrationTestImplementation(libs.hikaricp)
    // integration: postgres
    integrationTestImplementation(libs.postgresql)
    integrationTestImplementation(libs.testcontainers.postgresql)
    // integration: mysql
    integrationTestImplementation(libs.mysql)
    integrationTestImplementation(libs.testcontainers.mysql)
}
