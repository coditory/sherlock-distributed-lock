// Prints project version.
// Usage: ./gradlew version --quiet
tasks.register("version") {
    doLast {
        println(project.version)
    }
}
