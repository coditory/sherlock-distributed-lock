plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiCommon)
    api(libs.rxjava)

    // implementation
    implementation(projects.common)

    // test
    testImplementation(projects.tests)

    // integration
    integrationTestImplementation(projects.inmem.inmemRxjava)
}
