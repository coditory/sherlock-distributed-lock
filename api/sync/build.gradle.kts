description = "Sherlock Distributed Lock synchronous API"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-common"))
    api("org.slf4j:slf4j-api:${versions["slf4j"]}")
    api("org.jetbrains:annotations:${versions["jetbrainsAnnotations"]}")

    // implementation
    implementation(project(":common"))

    // test
    testImplementation(project(":inmem:inmem-sync"))
}
