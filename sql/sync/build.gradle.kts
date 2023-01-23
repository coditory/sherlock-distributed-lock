description = "Sherlock Distributed Lock implementation using SQL synchronous connector"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-sync"))

    // implementation
    implementation(project(":common"))
    implementation(project(":sql:sql-common"))

    // tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions["junit"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versions["junit"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // integration
    integrationImplementation(project(":tests"))
    integrationImplementation("com.zaxxer:HikariCP:${versions["hikaricp"]}")
    // integration: postgres
    integrationImplementation("org.postgresql:postgresql:${versions["postgresql"]}")
    integrationImplementation("org.testcontainers:postgresql:${versions["testContainers"]}")
    // integration: mysql
    integrationImplementation("mysql:mysql-connector-java:${versions["mysql"]}")
    integrationImplementation("org.testcontainers:mysql:${versions["testContainers"]}")
}