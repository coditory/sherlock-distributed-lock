rootProject.name = "sherlock"
include("api")
include("api:api-common")
include("api:api-sync")
include("api:api-reactive")
include("api:api-reactor")
include("api:api-rxjava")
include("api:api-tests")
include("sample")
include("common")
include("mongo")
include("mongo:mongo-sync")
include("mongo:mongo-reactive")
include("mongo:mongo-common")
include("inmem:inmem-sync")
include("inmem:inmem-reactive")
include("inmem:inmem-common")
include("sql")
include("tests")

// Alias node names so all are unique
// Fix for https://github.com/gradle/gradle/issues/847
project(":api:api-common").projectDir = file("./api/common")
project(":api:api-sync").projectDir = file("./api/sync")
project(":api:api-reactive").projectDir = file("./api/reactive")
project(":api:api-reactor").projectDir = file("./api/reactor")
project(":api:api-rxjava").projectDir = file("./api/rxjava")
project(":api:api-tests").projectDir = file("./api/tests")
project(":mongo:mongo-sync").projectDir = file("./mongo/sync")
project(":mongo:mongo-reactive").projectDir = file("./mongo/reactive")
project(":mongo:mongo-common").projectDir = file("./mongo/common")
project(":inmem:inmem-sync").projectDir = file("./inmem/sync")
project(":inmem:inmem-reactive").projectDir = file("./inmem/reactive")
project(":inmem:inmem-common").projectDir = file("./inmem/common")

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

plugins {
  id("com.gradle.enterprise").version("3.2")
}

