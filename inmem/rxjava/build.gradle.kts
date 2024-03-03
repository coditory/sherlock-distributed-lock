plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiRxjava)
    api(projects.inmem.inmemSync)
    integrationTestImplementation(projects.tests)
}
