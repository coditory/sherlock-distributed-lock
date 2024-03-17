plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCoroutines)
    api(projects.api.apiCoroutinesConnector)
    implementation(projects.connectors.inmem.inmemCommon)
    integrationTestImplementation(projects.tests)
}
