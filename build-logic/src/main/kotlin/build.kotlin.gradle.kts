import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("build.java")
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

ktlint {
    version.set(libs.versions.ktlint.get())
}

dependencies {
    // implementation
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)

    // test
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
}