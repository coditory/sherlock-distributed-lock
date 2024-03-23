plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.mongo.mongoReactor)
    implementation(libs.mongodb.reactivestreams)
    implementation(libs.logback.classic)
}
