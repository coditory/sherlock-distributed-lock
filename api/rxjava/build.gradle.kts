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
    testImplementation(projects.connectors.inmem.inmemRxjava)
}
