// This module is NOT a public API - imported as implementation

plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.common)
    api(libs.mongodb.core)
}
