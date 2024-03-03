plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiRxjava)
    api(libs.mongodb.reactivestreams)
    implementation(projects.mongo.mongoCommon)
    integrationTestImplementation(projects.mongo.mongoCommonTests)
}
