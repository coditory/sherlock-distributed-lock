description = "Sherlock Distributed Lock with Reactor based API"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api("io.projectreactor:reactor-core:${versions["reactor"]}")
    api(project(":api:api-common"))

    // implementation
    implementation(project(":common"))

    // integration
    integrationImplementation(project(":inmem:inmem-reactor"))
    integrationImplementation(project(":inmem:inmem-common"))
    integrationImplementation(project(":tests"))
}
