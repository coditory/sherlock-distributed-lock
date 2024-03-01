import java.io.IOException

require(project == rootProject) { "build.version is applicable to rootProject only" }

val textVersion = project.version.toString()
if (textVersion == "unspecified") {
    version = Semver.fromGitTag().nextPatchSnapshot().toString()
} else {
    Semver.parse(textVersion) ?: "Could not parse project version: $textVersion"
}

allprojects {
    version = rootProject.version
}

tasks.register("version") {
    doLast {
        println(project.version)
    }
}

data class Semver(
    private val major: Int,
    private val minor: Int,
    private val patch: Int,
    private val suffix: String? = null,
) : Comparable<Semver> {
    override fun compareTo(other: Semver): Int {
        if (major > other.major) return 1
        if (major < other.major) return -1
        if (minor > other.minor) return 1
        if (minor < other.minor) return -1
        if (patch > other.patch) return 1
        if (patch < other.patch) return -1
        if (suffix != null && other.suffix == null) return 1
        if (suffix == null && other.suffix != null) return -1
        if (suffix != null && other.suffix != null) return suffix.compareTo(other.suffix)
        return 0
    }

    fun nextPatchSnapshot(): Semver {
        return copy(
            patch = patch + 1,
            suffix = "SNAPSHOT"
        )
    }

    override fun toString(): String {
        return if (suffix.isNullOrEmpty()) {
            "$major.$minor.$patch"
        } else {
            "$major.$minor.$patch-$suffix"
        }
    }

    fun tagName(): String {
        return "v${toString()}"
    }

    companion object {
        private val REGEX = Regex("v?([0-9]+)\\.([0-9]+)\\.([0-9]+)(-.+)?")

        fun parse(text: String, strict: Boolean = true): Semver? {
            val groups = REGEX.matchEntire(text)?.groups ?: return null
            if (groups.size < 4 || groups.size > 5) return null
            if (strict && groups[0]?.value?.startsWith("v") == true) return null
            return Semver(
                major = groups[1]?.value?.toIntOrNull() ?: return null,
                minor = groups[2]?.value?.toIntOrNull() ?: return null,
                patch = groups[3]?.value?.toIntOrNull() ?: return null,
                suffix = groups[4]?.value?.trimStart('-'),
            )
        }

        fun fromGitTag(): Semver {
            return runCommand("git tag -l 'v[0-9]*.[0-9]*.[0-9]*' --sort=-v:refname | head -n 1")
                .split('\n')
                .mapNotNull { parse(it, strict = false) }
                .minOrNull()
                ?: Semver(0, 0, 0)
        }

        private fun runCommand(
            command: String,
            workingDir: File = File("."),
            timeoutAmount: Long = 60,
            timeoutUnit: TimeUnit = TimeUnit.SECONDS
        ): String = ProcessBuilder("sh", "-c", command)
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .apply { waitFor(timeoutAmount, timeoutUnit) }
            .run {
                val error = errorStream.bufferedReader().readText().trim()
                if (error.isNotEmpty()) {
                    throw IOException(error)
                }
                inputStream.bufferedReader().readText().trim()
            }
    }
}
