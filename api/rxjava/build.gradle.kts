plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCommon)
    api(libs.rxjava)
    implementation(projects.common)
    testImplementation(projects.tests)
    integrationTestImplementation(projects.connectors.inmem.inmemRxjava)
}
