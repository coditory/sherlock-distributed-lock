plugins {
    id("build.java")
}

dependencies {
    api(projects.common)
    api(projects.tests)
    api(projects.mongo.mongoCommon)
    api(libs.mongodb.sync)
    api(libs.spock.core)
    api(libs.jsonassert)
    implementation(libs.testcontainers.mongodb)
}
