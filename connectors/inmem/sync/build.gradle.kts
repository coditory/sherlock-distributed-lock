plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiSync)
    implementation(projects.connectors.inmem.inmemCommon)
    integrationTestImplementation(projects.tests)
}
