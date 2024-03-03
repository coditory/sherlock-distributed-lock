plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCoroutine)
    api(projects.api.apiCoroutineConnector)
    implementation(projects.inmem.inmemCommon)
    integrationTestImplementation(projects.tests)
}
