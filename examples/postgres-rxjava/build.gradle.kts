plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.sql.sqlRxjava)
    implementation(libs.r2dbc.postgresql)
    implementation(libs.postgresql)
    implementation(libs.logback.classic)
    implementation(libs.hikaricp)
}
