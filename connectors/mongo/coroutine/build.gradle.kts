plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCoroutine)
    api(projects.api.apiCoroutineConnector)
    implementation(projects.connectors.mongo.mongoCommon)
    implementation(libs.mongodb.coroutine)
    integrationTestImplementation(projects.connectors.mongo.mongoTests)
}
