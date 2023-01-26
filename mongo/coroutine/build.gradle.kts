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

    // integration
    integrationTestImplementation(projects.tests)
    integrationTestImplementation(libs.testcontainers.mongodb)
}
