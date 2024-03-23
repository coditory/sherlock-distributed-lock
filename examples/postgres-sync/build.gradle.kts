plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.sql.sqlSync)
    implementation(libs.postgresql)
    implementation(libs.logback.classic)
    implementation(libs.hikaricp)
}
