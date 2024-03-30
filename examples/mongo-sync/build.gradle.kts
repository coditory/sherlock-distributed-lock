plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.mongo.mongoSync)
    implementation(libs.logback.classic)
}
