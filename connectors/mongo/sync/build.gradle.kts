plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiSync)
    implementation(libs.mongodb.sync)
    implementation(projects.connectors.mongo.mongoCommon)
    integrationTestImplementation(projects.connectors.mongo.mongoTests)
}
