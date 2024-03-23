plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.sql.sqlSync)
    implementation(libs.mysql)
    implementation(libs.logback.classic)
    implementation(libs.hikaricp)
}
