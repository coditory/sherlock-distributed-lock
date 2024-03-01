plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiCommon)
    api(projects.api.apiCoroutine)

    // implementation
    implementation(projects.common)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)

    // test
    testImplementation(projects.inmem.inmemCoroutine)
    integrationTestImplementation(projects.tests)
}
