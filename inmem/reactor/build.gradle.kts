plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiReactor)
    api(projects.inmem.inmemSync)

    // integration
    integrationTestImplementation(projects.tests)
}
