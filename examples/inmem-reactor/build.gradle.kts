plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.inmem.inmemReactor)
    implementation(libs.logback.classic)
}
