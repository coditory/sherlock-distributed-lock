plugins {
    id("build.kotlin")
    id("build.publish")
    id("build.coverage")
}

dependencies {
    api(projects.api.apiCommon)
    implementation(projects.common)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(projects.tests)
    testImplementation(projects.connectors.inmem.inmemCoroutine)
}

tasks.named<GroovyCompile>("compileTestGroovy") {
    // make groovy test see kotlin test
    classpath += files(tasks.compileTestKotlin)
}
