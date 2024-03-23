plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.mongo.mongoReactor)
    implementation(libs.logback.classic)
}
