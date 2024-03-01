@file:Suppress("UnstableApiUsage", "HasPlatformType")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java-library")
    id("groovy")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

testing {
    suites {
        val test by getting(JvmTestSuite::class)

        val integrationTest by registering(JvmTestSuite::class) {
            testType.set(TestSuiteType.INTEGRATION_TEST)

            val mainSourceSet = project.sourceSets.main.get()
            val testSourceSet = project.sourceSets.test.get()

            sources {
                compileClasspath += testSourceSet.output +
                    mainSourceSet.output + testSourceSet.compileClasspath
                runtimeClasspath += testSourceSet.output +
                    mainSourceSet.output + testSourceSet.runtimeClasspath
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events =
            setOf(
                TestLogEvent.FAILED,
                TestLogEvent.STANDARD_ERROR,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.SKIPPED,
            )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<GroovyCompile>().configureEach {
    groovyOptions.encoding = "UTF-8"
}

dependencies {
    // test
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.spock.core)
}
