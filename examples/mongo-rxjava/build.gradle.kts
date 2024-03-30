plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.mongo.mongoRxjava)
    implementation(libs.logback.classic)
}
