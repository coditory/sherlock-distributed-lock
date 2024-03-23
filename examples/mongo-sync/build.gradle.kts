plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.mongo.mongoSync)
    implementation(libs.mongodb.sync)
    implementation(libs.logback.classic)
}
