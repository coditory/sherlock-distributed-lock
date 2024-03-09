plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCommon)
    implementation(projects.common)
    testImplementation(projects.tests)
    testImplementation(projects.connectors.inmem.inmemSync)
}
