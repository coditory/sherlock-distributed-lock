plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiCoroutine)
    api(projects.inmem.inmemCommon)

    // implementation
    implementation(projects.api.apiCoroutineConnector)

    // integration
    integrationTestImplementation(projects.tests)
}
