plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    implementation(projects.common)
}
