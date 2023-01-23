description = "Sherlock Distributed Lock shared tests"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    api(project(":api:api-common"))
    api(project(":api:api-sync"))
    api(project(":api:api-reactor"))
    api(project(":api:api-rxjava"))
    api(project(":common"))
    api("org.spockframework:spock-core:${versions["spock"]}")
    api("org.skyscreamer:jsonassert:${versions["jsonAssert"]}")
    api("org.awaitility:awaitility:${versions["awaitility"]}")
}
