description = "Sherlock Distributed Lock implementation for Reactor using SQL asynchronous connector R2DBC"

dependencies {
    val versions = rootProject.ext["versions"] as Map<*, *>

    // api
    api(project(":api:api-reactor"))
    api("io.r2dbc:r2dbc-spi:${versions["r2dbc"]}")

    // implementation
    implementation(project(":common"))
    implementation(project(":sql:sql-common"))

    // tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions["junit"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versions["junit"]}")

    // integration
    integrationImplementation(project(":tests"))
    integrationImplementation("io.r2dbc:r2dbc-pool:${versions["r2dbc"]}")
    // integration: postgres
    integrationImplementation("org.postgresql:postgresql:${versions["postgresql"]}")
    integrationImplementation("org.postgresql:r2dbc-postgresql:${versions["r2dbc"]}")
    integrationImplementation("org.testcontainers:postgresql:${versions["testContainers"]}")
    // integration: mysql
    integrationImplementation("mysql:mysql-connector-java:${versions["mysql"]}")
    // integrationImplementation("dev.miku:r2dbc-mysql:${versions["r2dbc"]}") // waiting fo release
    integrationImplementation("org.testcontainers:mysql:${versions["testContainers"]}")
}
