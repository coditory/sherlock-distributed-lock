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
    integrationTestImplementation(projects.sql.sqlCommonTests)
}
