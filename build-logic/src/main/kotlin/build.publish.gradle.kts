plugins {
    `java-library`
    `maven-publish`
    signing
}

// creating publishable jar introduces time overhead
// add "publish" property to enable signing and javadoc and sources in the jar
// ./gradlew ... -Ppublish
// ...or with a task
// ./gradlew ... publishToSonatype
val publishEnabled = (project.hasProperty("publish") && project.properties["publish"] != "false") ||
    project.gradle.startParameter.taskNames.contains("publishAllToSonatype") ||
    project.gradle.startParameter.taskNames.contains("publishAllToMavenLocal")

java {
    if (publishEnabled) {
        withSourcesJar()
        withJavadocJar()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.coditory.sherlock"
            artifactId = project.name
            from(components["java"])

            pom {
                name.set(project.name)
                description.set("Distributed Lock Library for JVM")
                url.set("https://github.com/coditory/sherlock-distributed-lock")
                organization {
                    name.set("Coditory")
                    url.set("https://coditory.com")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("coditory")
                        name.set("Coditory")
                        email.set("admin@coditory.com")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:coditory/sherlock-distributed-lock.git")
                    developerConnection.set("scm:git@github.com:coditory/sherlock-distributed-lock.git")
                    url.set("https://github.com/coditory/sherlock-distributed-lock")
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/coditory/sherlock-distributed-lock/issues")
                }
            }
        }
    }
}

if (publishEnabled && !project.gradle.startParameter.taskNames.contains("publishAllToMavenLocal")) {
    val signingKey: String? = System.getenv("SIGNING_KEY")
    val signingPwd: String? = System.getenv("SIGNING_PASSWORD")
    if (signingKey.isNullOrBlank() || signingPwd.isNullOrBlank()) {
        logger.info("Signing disabled as the GPG key was not found. Define SIGNING_KEY and SIGNING_PASSWORD to enable.")
    }
    signing {
        useInMemoryPgpKeys(signingKey, signingPwd)
        sign(publishing.publications["maven"])
    }
}
