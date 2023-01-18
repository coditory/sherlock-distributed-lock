# Contributing

## Commit messages
Before writing a commit message read [this article](https://chris.beams.io/posts/git-commit/).

## Build
Before pushing any changes make sure project builds without errors with:
```
./gradlew build
```

## Unit tests
We use [Spock](https://spockframework.org) for testing.
Please use the `Spec.groovy` suffix on new test classes.

## Validate changes locally
Before submitting a pull request test your changes locally on a sample project.
There are few ways for local testing:
- simply use the [sample subproject](https://github.com/coditory/sherlock-distributed-lock/tree/master/sample)
- or publish library to maven local repository with `./gradlew publishToMavenLocal`
