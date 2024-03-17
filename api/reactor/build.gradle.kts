plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCommon)
    api(libs.reactor.core)
    implementation(projects.common)
    testImplementation(projects.tests)
    testImplementation(projects.connectors.inmem.inmemReactor)
}
