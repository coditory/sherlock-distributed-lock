rootProject.name = "sherlock"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("build-logic")

include("common")
include("api")
include("api:api-common")
include("api:api-sync")
include("api:api-reactor")
include("api:api-rxjava")
include("api:api-coroutine")
include("api:api-coroutine-connector")
include("sample")
include("mongo")
include("mongo:mongo-sync")
include("mongo:mongo-reactor")
include("mongo:mongo-rxjava")
include("mongo:mongo-coroutine")
include("mongo:mongo-common")
include("inmem:inmem-sync")
include("inmem:inmem-reactor")
include("inmem:inmem-rxjava")
include("inmem:inmem-common")
include("inmem:inmem-coroutine")
include("sql")
include("sql:sql-common")
include("sql:sql-sync")
include("sql:sql-reactor")
include("sql:sql-rxjava")
include("sql:sql-coroutine")
include("tests")

// Alias node names so all are unique
// Fix for https://github.com/gradle/gradle/issues/847
project(":api:api-common").projectDir = file("./api/common")
project(":api:api-sync").projectDir = file("./api/sync")
project(":api:api-reactor").projectDir = file("./api/reactor")
project(":api:api-rxjava").projectDir = file("./api/rxjava")
project(":api:api-coroutine").projectDir = file("./api/coroutine")
project(":api:api-coroutine-connector").projectDir = file("./api/coroutine-connector")
project(":mongo:mongo-sync").projectDir = file("./mongo/sync")
project(":mongo:mongo-reactor").projectDir = file("./mongo/reactor")
project(":mongo:mongo-rxjava").projectDir = file("./mongo/rxjava")
project(":mongo:mongo-coroutine").projectDir = file("./mongo/coroutine")
project(":mongo:mongo-common").projectDir = file("./mongo/common")
project(":inmem:inmem-sync").projectDir = file("./inmem/sync")
project(":inmem:inmem-reactor").projectDir = file("./inmem/reactor")
project(":inmem:inmem-rxjava").projectDir = file("./inmem/rxjava")
project(":inmem:inmem-coroutine").projectDir = file("./inmem/coroutine")
project(":inmem:inmem-common").projectDir = file("./inmem/common")
project(":sql:sql-sync").projectDir = file("./sql/sync")
project(":sql:sql-reactor").projectDir = file("./sql/reactor")
project(":sql:sql-rxjava").projectDir = file("./sql/rxjava")
project(":sql:sql-coroutine").projectDir = file("./sql/coroutine")
project(":sql:sql-common").projectDir = file("./sql/common")

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise").version("3.12.2")
}

gradleEnterprise {
    if (!System.getenv("CI").isNullOrEmpty()) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
