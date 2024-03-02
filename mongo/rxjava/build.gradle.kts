plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiRxjava)
    api(libs.mongodb.reactivestreams)

    // implementation
    implementation(projects.mongo.mongoCommon)

    // integration
    integrationTestImplementation(projects.tests)
    integrationTestImplementation(projects.mongo.mongoCommonTests)
}
