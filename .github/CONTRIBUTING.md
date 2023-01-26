# Contributing

## Build
Before pushing any changes make sure project builds without errors with:
`./gradlew build`

## Unit tests
This project uses [Spock](https://spockframework.org) for testing.
Please use the `Spec.groovy` suffix on new test classes.

Pull requests that lower test coverage will not be merged.
Test coverage metric will be visible on GitHub Pull request page.
It can be also generated in IDE or via command line with `./gradlew build coverage`
- check html report in `build/report/jacoco/coverage/html`.

## Validate changes locally
Before submitting a pull request test your changes locally on a sample project.
You can test locally by publishing this library to maven local repository with
`./gradlew publishAllToMavenLocal -Pversion=...`.

## Formatting
There are no enforced code style rules for Java and Groovy sources.
Just please use IntelliJ code styles from "Project scheme" (`.idea/codeStyles`).

Kotlin codestyle is enforced by [Ktlint](https://pinterest.github.io/ktlint/).
Ktlint rules are already propagated to `.idea/codeStyles`.
You can validate Kotlin code style in command line with
`./gradlew ktlintCheck`.

## Commit messages
Before writing a commit message read [this article](https://chris.beams.io/posts/git-commit/).

## Validating with snapshot release
Snapshot release is triggered manually by code owners.
To use a released snapshot version make sure to register Sonatype snapshot repository in gradle with:

```
// build.gradle.kts
repositories {
    mavenCentral()
    maven {
        url = URI("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
```
