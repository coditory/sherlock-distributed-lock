plugins {
    id("java-library")
    id("build.test")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    api(libs.slf4j.api)
    api(libs.jetbrains.annotations)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Werror", "-Xlint", "-Xlint:-serial"))
}

// make javadoc less strict to limit noisy logs
tasks.withType<Javadoc>().configureEach {
    val sourceSet = project.extensions.getByType<JavaPluginExtension>().sourceSets.getByName("main")
    source = sourceSet.allJava
    classpath = sourceSet.compileClasspath
    isFailOnError = false
    options {
        this as StandardJavadocDocletOptions
        addBooleanOption("Xdoclint:none", true)
        addStringOption("Xmaxwarns", "1")
        memberLevel = JavadocMemberLevel.PUBLIC
        outputLevel = JavadocOutputLevel.QUIET
        encoding = "UTF-8"
    }
}
