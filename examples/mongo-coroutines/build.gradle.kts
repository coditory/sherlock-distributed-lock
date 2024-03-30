plugins {
    id("build.kotlin")
}

dependencies {
    implementation(projects.connectors.mongo.mongoCoroutines)
    implementation(libs.logback.classic)
}
