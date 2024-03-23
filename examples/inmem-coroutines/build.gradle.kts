plugins {
    id("build.kotlin")
}

dependencies {
    implementation(projects.connectors.inmem.inmemCoroutines)
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.coroutines.core)
}
