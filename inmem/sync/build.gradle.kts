plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiSync)
    api(projects.inmem.inmemCommon)

    // integration
    integrationTestImplementation(projects.tests)
}
