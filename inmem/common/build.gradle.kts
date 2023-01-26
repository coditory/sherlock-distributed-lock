plugins {
    id("build.java")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.common)
    integrationTestImplementation(projects.tests)
}
