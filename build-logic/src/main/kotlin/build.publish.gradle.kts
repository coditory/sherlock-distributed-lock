plugins {
    `java-library`
    `maven-publish`
    signing
}

publishing {
    val artifactName = "sherlock-${project.name.removeSuffix("-sync")}"
    publications {
        create<MavenPublication>("jvm") {
            groupId = "com.coditory.sherlock"
            artifactId = artifactName
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set(artifactName)
                description.set(project.description ?: rootProject.description ?: "Distributed Lock Library for JVM")
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

signing {
    if (System.getenv("SIGNING_KEY")?.isNotBlank() == true && System.getenv("SIGNING_PASSWORD")?.isNotBlank() == true) {
        useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
    }
    sign(publishing.publications["jvm"])
}
