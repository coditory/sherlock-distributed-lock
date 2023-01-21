rootProject.name = "sherlock"
include("common")
include("api")
include("api:api-sync")
include("api:api-reactor")
include("api:api-rxjava")
include("sample")
include("mongo")
include("mongo:mongo-sync")
include("mongo:mongo-reactor")
include("mongo:mongo-rxjava")
include("mongo:mongo-common")
include("inmem:inmem-sync")
include("inmem:inmem-reactor")
include("inmem:inmem-rxjava")
include("inmem:inmem-common")
include("sql")
include("sql:sql-common")
include("sql:sql-sync")
include("sql:sql-reactor")
include("sql:sql-rxjava")
include("tests")

// Alias node names so all are unique
// Fix for https://github.com/gradle/gradle/issues/847
project(":api:api-sync").projectDir = file("./api/sync")
project(":api:api-reactor").projectDir = file("./api/reactor")
project(":api:api-rxjava").projectDir = file("./api/rxjava")
project(":mongo:mongo-sync").projectDir = file("./mongo/sync")
project(":mongo:mongo-reactor").projectDir = file("./mongo/reactor")
project(":mongo:mongo-rxjava").projectDir = file("./mongo/rxjava")
project(":mongo:mongo-common").projectDir = file("./mongo/common")
project(":inmem:inmem-sync").projectDir = file("./inmem/sync")
project(":inmem:inmem-reactor").projectDir = file("./inmem/reactor")
project(":inmem:inmem-rxjava").projectDir = file("./inmem/rxjava")
project(":inmem:inmem-common").projectDir = file("./inmem/common")
project(":sql:sql-sync").projectDir = file("./sql/sync")
project(":sql:sql-reactor").projectDir = file("./sql/reactor")
project(":sql:sql-rxjava").projectDir = file("./sql/rxjava")
project(":sql:sql-common").projectDir = file("./sql/common")

plugins {
    id("com.gradle.enterprise").version("3.12.2")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"

        if (!System.getenv("CI").isNullOrEmpty()) {
            publishAlways()
            tag("CI")
        }
    }
}

