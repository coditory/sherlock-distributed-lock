plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.inmem.inmemRxjava)
    implementation(libs.logback.classic)
}
