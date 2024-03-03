plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiReactor)
    api(libs.mongodb.reactivestreams)
    implementation(projects.mongo.mongoCommon)
    integrationTestImplementation(projects.mongo.mongoCommonTests)
}
