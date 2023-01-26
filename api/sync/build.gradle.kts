plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiCommon)

    // implementation
    implementation(projects.common)

    // test
    testImplementation(projects.tests)
    testImplementation(projects.inmem.inmemSync)
}
