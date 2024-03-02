plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // api
    api(projects.api.apiSync)
    api(libs.mongodb.sync)

    // implementation
    implementation(projects.mongo.mongoCommon)

    // integration
    integrationTestImplementation(projects.mongo.mongoCommonTests)
    integrationTestImplementation(projects.tests)
}
