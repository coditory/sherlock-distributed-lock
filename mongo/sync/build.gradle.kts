plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiSync)
    api(libs.mongodb.sync)
    implementation(projects.mongo.mongoCommon)
    integrationTestImplementation(projects.mongo.mongoCommonTests)
}
