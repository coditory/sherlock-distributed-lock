plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCoroutine)
    api(projects.api.apiCoroutineConnector)
    implementation(projects.mongo.mongoCommon)
    implementation(libs.mongodb.coroutine)
    integrationTestImplementation(projects.mongo.mongoCommonTests)
}
