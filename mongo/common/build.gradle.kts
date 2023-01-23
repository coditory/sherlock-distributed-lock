description = "Sherlock Distributed Lock common packages for mongo implementations"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>
    api(project(":common"))
    api("org.mongodb:mongodb-driver-core:${versions["mongodb"]}")
}
