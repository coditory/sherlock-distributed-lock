plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiSync)
    implementation(projects.sql.sqlCommon)
    integrationTestImplementation(projects.sql.sqlCommonTests)
}
