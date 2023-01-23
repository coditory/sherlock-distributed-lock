description = "Sherlock Distributed Lock implementation for RxJava using mongo reactive driver"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-rxjava"))
    api("io.projectreactor:reactor-core:${versions["reactor"]}")
    api("org.mongodb:mongodb-driver-reactivestreams:${versions["mongodbReactive"]}")

    // implementation
    implementation(project(":mongo:mongo-common"))

    // integration
    integrationImplementation(project(":tests"))
    integrationImplementation("org.testcontainers:mongodb:${versions["testContainers"]}")
}
