plugins {
    id("jacoco")
}

tasks.register<JacocoReport>("coverage") {
    description = "Creates combined coverage report"

    val coveredProjects = subprojects
        .filter { it.plugins.hasPlugin("jacoco") }

    coveredProjects
        .forEach { subproject ->
            executionData(fileTree(subproject.buildDir).include("jacoco/*.exec"))
            sourceSets(subproject.extensions.getByType(JavaPluginExtension::class).sourceSets.getByName("main"))
        }

    dependsOn(
        coveredProjects.map { it.tasks.findByName("test") }
            + coveredProjects.map { it.tasks.findByName("integrationTest") }
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
