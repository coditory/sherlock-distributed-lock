plugins {
    `kotlin-dsl`
    kotlin("jvm") version embeddedKotlinVersion
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.kotlin)
    implementation(libs.gradle.ktlint)
    implementation(libs.gradle.nexusPublish)
    compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}
