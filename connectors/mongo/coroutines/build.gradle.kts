plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCoroutines)
    api(projects.api.apiCoroutinesConnector)
    implementation(projects.connectors.mongo.mongoCommon)
    implementation(libs.mongodb.coroutine)
    integrationTestImplementation(projects.connectors.mongo.mongoTests)
}
