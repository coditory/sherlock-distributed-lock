plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiCommon)

    // implementation
    implementation(projects.common)

    // integration
    integrationTestImplementation(projects.tests)
}
