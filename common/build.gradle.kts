plugins {
    id("build.java")
    id("build.coverage")
    id("build.publish")
}

dependencies {
    api(libs.slf4j.api)
    api(libs.jetbrains.annotations)
}
