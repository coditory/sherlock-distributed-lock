import java.time.Duration

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
    connectTimeout.set(Duration.ofMinutes(5))
    clientTimeout.set(Duration.ofMinutes(5))
    repositories {
        sonatype {
            System.getenv("NEXUS_USERNAME")?.let { username.set(it) }
            System.getenv("NEXUS_PASSWORD")?.let { password.set(it) }
        }
    }
}

tasks.register("publishAllToMavenLocal") {
    description = "Publish all the projects to Maven Local"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishToMavenLocal"))
        }
    }
}

if (!System.getenv("CI").isNullOrEmpty()) {
    tasks.register("publishAllToSonatype") {
        description = "Publish all the projects to Sonatype Repository"
        subprojects {
            if (this.plugins.hasPlugin("publishing")) {
                dependsOn(tasks.named("publishToSonatype"))
            }
        }
    }
}
