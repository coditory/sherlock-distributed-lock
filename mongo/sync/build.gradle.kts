description = "Sherlock Distributed Lock implementation using mongo synchronous connector"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-sync"))
    api("org.mongodb:mongodb-driver-sync:${versions["mongodb"]}")

    // implementation
    implementation(project(":mongo:mongo-common"))

    // integration
    integrationImplementation(project(":tests"))
    integrationImplementation("org.testcontainers:mongodb:${versions["testContainers"]}")
}