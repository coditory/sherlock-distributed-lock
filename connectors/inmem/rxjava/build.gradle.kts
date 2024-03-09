plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiRxjava)
    api(projects.connectors.inmem.inmemSync)
    implementation(projects.connectors.inmem.inmemCommon)
    integrationTestImplementation(projects.tests)
}
