plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiCoroutine)
    api(projects.inmem.inmemCommon)
    api(projects.api.apiCoroutineConnector)

    // integration
    integrationTestImplementation(projects.tests)
}
