plugins {
    id("build.kotlin")
}

dependencies {
    implementation(projects.connectors.sql.sqlCoroutines)
    implementation(libs.r2dbc.mysql)
    implementation(libs.hikaricp)
    implementation(libs.logback.classic)
}
