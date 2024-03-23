plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiReactor)
    implementation(libs.mongodb.reactivestreams)
    implementation(projects.connectors.mongo.mongoCommon)
    integrationTestImplementation(projects.connectors.mongo.mongoTests)
}
