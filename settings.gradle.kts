rootProject.name = "sherlock"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("build-logic")
include(":common")
include(":api")
include(":api:api-common")
include(":api:api-sync")
include(":api:api-reactor")
include(":api:api-rxjava")
include(":api:api-coroutine")
include(":api:api-coroutine-connector")
include(":samples")
include(":connectors")
include(":connectors:mongo")
include(":connectors:mongo:mongo-common")
include(":connectors:mongo:mongo-tests")
include(":connectors:mongo:mongo-sync")
include(":connectors:mongo:mongo-reactor")
include(":connectors:mongo:mongo-rxjava")
include(":connectors:mongo:mongo-coroutine")
include(":connectors:inmem")
include(":connectors:inmem:inmem-common")
include(":connectors:inmem:inmem-sync")
include(":connectors:inmem:inmem-reactor")
include(":connectors:inmem:inmem-rxjava")
include(":connectors:inmem:inmem-coroutine")
include(":connectors:sql")
include(":connectors:sql:sql-common")
include(":connectors:sql:sql-tests")
include(":connectors:sql:sql-sync")
include(":connectors:sql:sql-reactor")
include(":connectors:sql:sql-rxjava")
include(":connectors:sql:sql-coroutine")
include(":tests")

// Alias node names so all are unique
// Fix for https://github.com/gradle/gradle/issues/847
project(":api:api-common").projectDir = file("./api/common")
project(":api:api-sync").projectDir = file("./api/sync")
project(":api:api-reactor").projectDir = file("./api/reactor")
project(":api:api-rxjava").projectDir = file("./api/rxjava")
project(":api:api-coroutine").projectDir = file("./api/coroutine")
project(":api:api-coroutine-connector").projectDir = file("./api/coroutine-connector")
project(":connectors:mongo:mongo-sync").projectDir = file("./connectors/mongo/sync")
project(":connectors:mongo:mongo-reactor").projectDir = file("./connectors/mongo/reactor")
project(":connectors:mongo:mongo-rxjava").projectDir = file("./connectors/mongo/rxjava")
project(":connectors:mongo:mongo-coroutine").projectDir = file("./connectors/mongo/coroutine")
project(":connectors:mongo:mongo-common").projectDir = file("./connectors/mongo/common")
project(":connectors:mongo:mongo-tests").projectDir = file("./connectors/mongo/tests")
project(":connectors:inmem:inmem-sync").projectDir = file("./connectors/inmem/sync")
project(":connectors:inmem:inmem-reactor").projectDir = file("./connectors/inmem/reactor")
project(":connectors:inmem:inmem-rxjava").projectDir = file("./connectors/inmem/rxjava")
project(":connectors:inmem:inmem-coroutine").projectDir = file("./connectors/inmem/coroutine")
project(":connectors:inmem:inmem-common").projectDir = file("./connectors/inmem/common")
project(":connectors:sql:sql-sync").projectDir = file("./connectors/sql/sync")
project(":connectors:sql:sql-reactor").projectDir = file("./connectors/sql/reactor")
project(":connectors:sql:sql-rxjava").projectDir = file("./connectors/sql/rxjava")
project(":connectors:sql:sql-coroutine").projectDir = file("./connectors/sql/coroutine")
project(":connectors:sql:sql-common").projectDir = file("./connectors/sql/common")
project(":connectors:sql:sql-tests").projectDir = file("./connectors/sql/tests")
project(":tests").projectDir = file("./tests")

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise").version("3.15.1")
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
