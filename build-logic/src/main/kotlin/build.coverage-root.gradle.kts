import gradle.kotlin.dsl.accessors._1d662f89dd4d27eb95f0093e004edd00.main
import gradle.kotlin.dsl.accessors._1d662f89dd4d27eb95f0093e004edd00.sourceSets

plugins {
    id("jacoco")
}

tasks.register<JacocoReport>("coverage") {
    description = "Creates combined coverage report"

    val coveredProjects =
        subprojects
            .filter { it.plugins.hasPlugin("jacoco") }

    coveredProjects
        .forEach { subproject ->
            executionData(fileTree(subproject.layout.buildDirectory).include("jacoco/*.exec"))
            sourceSets(subproject.sourceSets.main.get())
        }

    dependsOn(
        coveredProjects.map { it.tasks.findByName("test") } +
            coveredProjects.map { it.tasks.findByName("integrationTest") },
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
