plugins {
    id("build.kotlin")
}

dependencies {
    implementation(projects.connectors.mongo.mongoCoroutines)
    implementation(libs.mongodb.coroutine)
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.coroutines.core)
}
