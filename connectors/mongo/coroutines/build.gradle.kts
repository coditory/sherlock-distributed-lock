plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCoroutines)
    api(projects.api.apiCoroutinesConnector)
    api(libs.mongodb.coroutine)
    implementation(projects.connectors.mongo.mongoCommon)
    integrationTestImplementation(projects.connectors.mongo.mongoTests)
}
