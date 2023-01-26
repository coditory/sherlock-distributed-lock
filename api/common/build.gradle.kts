plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    // implementation
    implementation(projects.common)

    // test
    testImplementation(projects.tests)
}
