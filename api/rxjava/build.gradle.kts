description = "Sherlock Distributed Lock with Reactor based API"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api("io.reactivex.rxjava2:rxjava:${versions["rxjava"]}")
    api(project(":api:api-common"))

    // implementation
    implementation(project(":common"))

    // integration
    integrationImplementation(project(":inmem:inmem-rxjava"))
    integrationImplementation(project(":inmem:inmem-common"))
    integrationImplementation(project(":tests"))
}
