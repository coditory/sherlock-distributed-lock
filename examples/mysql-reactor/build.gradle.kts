plugins {
    id("build.java")
}

dependencies {
    implementation(projects.connectors.sql.sqlReactor)
    implementation(libs.r2dbc.mysql)
    implementation(libs.mysql)
    implementation(libs.logback.classic)
    implementation(libs.hikaricp)
}
