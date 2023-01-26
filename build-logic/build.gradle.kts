plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.kotlin)
    implementation(libs.gradle.ktlint)
    implementation(libs.gradle.nexus.publish)
    compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}
