plugins {
    id("build.java")
}

dependencies {
    // sync
    implementation(projects.inmem.inmemSync)
    implementation(projects.mongo.mongoSync)
    implementation(projects.sql.sqlSync)
    // reactor
    implementation(projects.api.apiReactor)
    implementation(projects.inmem.inmemReactor)
    implementation(projects.mongo.mongoReactor)
    implementation(projects.sql.sqlReactor)
    // rxjava
    implementation(projects.api.apiRxjava)
    implementation(projects.inmem.inmemRxjava)
    implementation(projects.mongo.mongoRxjava)
    // drivers
    implementation(libs.postgresql)
    implementation(libs.mysql)
    // other
    implementation(libs.logback.core)
    implementation(libs.logback.classic)
    implementation(libs.hikaricp)
}
