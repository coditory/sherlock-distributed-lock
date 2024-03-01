import gradle.kotlin.dsl.accessors._84410e5fc98c9d61c82e9a615fee1e2f.main
import gradle.kotlin.dsl.accessors._84410e5fc98c9d61c82e9a615fee1e2f.sourceSets

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
            println(">>> Covered: " + subproject.name)
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
