// Prints project version.
// Usage: ./gradlew version --quiet
tasks.register("version") {
    val version = project.version
    doLast { println(version) }
}
