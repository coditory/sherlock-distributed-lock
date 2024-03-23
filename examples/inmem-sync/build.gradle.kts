plugins {
    id("build.java")
    id("build.kotlin")
}

dependencies {
    implementation(projects.connectors.inmem.inmemSync)
    implementation(libs.logback.classic)
}
