plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiReactor)
    api(libs.mongodb.reactivestreams)

    // implementation
    implementation(projects.mongo.mongoCommon)

    // integration
    integrationTestImplementation(projects.tests)
    integrationTestImplementation(libs.testcontainers.mongodb)
}
