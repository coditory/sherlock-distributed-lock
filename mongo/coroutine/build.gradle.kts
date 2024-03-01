plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiCoroutine)
    api(libs.mongodb.reactivestreams)

    // implementation
    implementation(projects.api.apiCoroutineConnector)
    implementation(projects.mongo.mongoCommon)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)

    // integration
    integrationTestImplementation(projects.tests)
    integrationTestImplementation(libs.testcontainers.mongodb)
}
