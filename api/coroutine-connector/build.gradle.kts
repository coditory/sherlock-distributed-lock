plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCommon)
    api(projects.api.apiCoroutine)
    implementation(projects.common)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    testImplementation(projects.connectors.inmem.inmemCoroutine)
    integrationTestImplementation(projects.tests)
}
