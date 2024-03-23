plugins {
    id("build.kotlin")
}

dependencies {
    implementation(projects.connectors.sql.sqlCoroutines)
    implementation(libs.r2dbc.postgresql)
    implementation(libs.hikaricp)
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.coroutines.core)
}
