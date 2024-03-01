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
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)

    // test
    testImplementation(libs.kotlinx.coroutines.test)

    // integration
    integrationTestImplementation(projects.tests)
}
