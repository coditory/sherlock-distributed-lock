plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiReactor)
    api(projects.inmem.inmemSync)
    integrationTestImplementation(projects.tests)
}
