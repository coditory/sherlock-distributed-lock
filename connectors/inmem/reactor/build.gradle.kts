plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiReactor)
    api(projects.connectors.inmem.inmemSync)
    implementation(projects.connectors.inmem.inmemCommon)
    integrationTestImplementation(projects.tests)
}
