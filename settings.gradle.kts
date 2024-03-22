rootProject.name = "sherlock"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("build-logic")
include(":api")
include(":api:api-common")
include(":api:api-coroutines")
include(":api:api-coroutines-connector")
include(":api:api-reactor")
include(":api:api-rxjava")
include(":api:api-sync")
include(":common")
include(":connectors")
include(":connectors:inmem")
include(":connectors:inmem:inmem-common")
include(":connectors:inmem:inmem-coroutines")
include(":connectors:inmem:inmem-reactor")
include(":connectors:inmem:inmem-rxjava")
include(":connectors:inmem:inmem-sync")
include(":connectors:mongo")
include(":connectors:mongo:mongo-common")
include(":connectors:mongo:mongo-coroutines")
include(":connectors:mongo:mongo-reactor")
include(":connectors:mongo:mongo-rxjava")
include(":connectors:mongo:mongo-sync")
include(":connectors:mongo:mongo-tests")
include(":connectors:sql")
include(":connectors:sql:sql-common")
include(":connectors:sql:sql-common-api")
include(":connectors:sql:sql-coroutines")
include(":connectors:sql:sql-reactor")
include(":connectors:sql:sql-rxjava")
include(":connectors:sql:sql-sync")
include(":connectors:sql:sql-tests")
include(":samples")
include(":tests")

// Alias node names so all are unique
// Fix for https://github.com/gradle/gradle/issues/847
project(":api:api-common").projectDir = file("./api/common")
project(":api:api-coroutines").projectDir = file("./api/coroutines")
project(":api:api-coroutines-connector").projectDir = file("./api/coroutines-connector")
project(":api:api-reactor").projectDir = file("./api/reactor")
project(":api:api-rxjava").projectDir = file("./api/rxjava")
project(":api:api-sync").projectDir = file("./api/sync")
project(":connectors:inmem:inmem-common").projectDir = file("./connectors/inmem/common")
project(":connectors:inmem:inmem-coroutines").projectDir = file("./connectors/inmem/coroutines")
project(":connectors:inmem:inmem-reactor").projectDir = file("./connectors/inmem/reactor")
project(":connectors:inmem:inmem-rxjava").projectDir = file("./connectors/inmem/rxjava")
project(":connectors:inmem:inmem-sync").projectDir = file("./connectors/inmem/sync")
project(":connectors:mongo:mongo-common").projectDir = file("./connectors/mongo/common")
project(":connectors:mongo:mongo-coroutines").projectDir = file("./connectors/mongo/coroutines")
project(":connectors:mongo:mongo-reactor").projectDir = file("./connectors/mongo/reactor")
project(":connectors:mongo:mongo-rxjava").projectDir = file("./connectors/mongo/rxjava")
project(":connectors:mongo:mongo-sync").projectDir = file("./connectors/mongo/sync")
project(":connectors:mongo:mongo-tests").projectDir = file("./connectors/mongo/tests")
project(":connectors:sql:sql-common").projectDir = file("./connectors/sql/common")
project(":connectors:sql:sql-common-api").projectDir = file("./connectors/sql/common-api")
project(":connectors:sql:sql-coroutines").projectDir = file("./connectors/sql/coroutines")
project(":connectors:sql:sql-reactor").projectDir = file("./connectors/sql/reactor")
project(":connectors:sql:sql-rxjava").projectDir = file("./connectors/sql/rxjava")
project(":connectors:sql:sql-sync").projectDir = file("./connectors/sql/sync")
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
