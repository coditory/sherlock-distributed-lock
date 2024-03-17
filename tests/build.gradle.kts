plugins {
    id("build.java")
}

dependencies {
    api(projects.api.apiCommon)
    api(projects.api.apiSync)
    api(projects.api.apiReactor)
    api(projects.api.apiRxjava)
    api(projects.api.apiCoroutine)
    api(projects.common)
    api(libs.spock.core)
    api(libs.jsonassert)
    api(libs.awaitility)
    api(libs.logback.core)
    api(libs.logback.classic)
}
