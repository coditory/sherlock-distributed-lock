plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiCoroutine)
    api(libs.mongodb.reactivestreams)
    api(projects.api.apiCoroutineConnector)

    // implementation
    implementation(projects.mongo.mongoCommon)
    implementation(libs.mongodb.coroutine)

    // integration
    integrationTestImplementation(projects.tests)
    integrationTestImplementation(projects.mongo.mongoCommonTests)
}
