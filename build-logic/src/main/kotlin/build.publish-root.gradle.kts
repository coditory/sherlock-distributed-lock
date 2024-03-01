import java.time.Duration

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
    connectTimeout.set(Duration.ofMinutes(5))
    clientTimeout.set(Duration.ofMinutes(5))
    repositories {
        sonatype {
            System.getenv("OSSRH_STAGING_PROFILE_ID")?.let { stagingProfileId = it }
            System.getenv("OSSRH_USERNAME")?.let { username.set(it) }
            System.getenv("OSSRH_PASSWORD")?.let { password.set(it) }
        }
    }
}
