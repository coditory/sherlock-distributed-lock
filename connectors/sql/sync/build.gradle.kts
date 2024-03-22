plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiSync)
    api(projects.connectors.sql.sqlCommonApi)
    implementation(projects.connectors.sql.sqlCommon)
    integrationTestImplementation(projects.connectors.sql.sqlTests)
}
