plugins {
    id("build.java")
}

dependencies {
    api(projects.common)
    api(projects.tests)
    api(projects.sql.sqlCommon)
    api(libs.spock.core)
    api(libs.hikaricp)
    // integration: postgres
    api(libs.postgresql)
    implementation(libs.testcontainers.postgresql)
    // integration: mysql
    api(libs.mysql)
    implementation(libs.testcontainers.mysql)
}
