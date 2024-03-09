plugins {
    id("build.java")
    id("build.kotlin")
}

dependencies {
    // sync
    implementation(projects.connectors.inmem.inmemSync)
    implementation(projects.connectors.mongo.mongoSync)
    implementation(projects.connectors.sql.sqlSync)
    // reactor
    implementation(projects.api.apiReactor)
    implementation(projects.connectors.inmem.inmemReactor)
    implementation(projects.connectors.mongo.mongoReactor)
    implementation(projects.connectors.sql.sqlReactor)
    // rxjava
    implementation(projects.api.apiRxjava)
    implementation(projects.connectors.inmem.inmemRxjava)
    implementation(projects.connectors.mongo.mongoRxjava)
    implementation(projects.connectors.sql.sqlRxjava)
    // coroutine
    implementation(projects.api.apiCoroutine)
    implementation(projects.connectors.inmem.inmemCoroutine)
    implementation(projects.connectors.mongo.mongoCoroutine)
    implementation(projects.connectors.sql.sqlCoroutine)
    implementation(libs.kotlinx.coroutines.core)
    // drivers
    implementation(libs.postgresql)
    implementation(libs.mysql)
    // other
    implementation(libs.logback.core)
    implementation(libs.logback.classic)
    implementation(libs.hikaricp)
}
