# Migrator

Sherlock comes with a migration mechanism, implemented as a lightweight wrapper on distributed locks.
Use this mechanism for a multi-step, one way migrations.

Sherlock migrator enforces following migration rules:

- migrations must not be run in parallel
- migration steps are applied in order
- if migration step succeeds it must never be run again
- migration process stops when first step fails

!!! info "Learn more"
    See full source code examples on  [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/example/).

## Annotated migration
Below example uses MongoDB, but sherlock migrator is available for all [connectors](connectors/index.md).

=== "Sync"
    ```java
    --8<-- "examples/mongo-sync/src/main/java/com/coditory/sherlock/samples/mongo/sync/MongoSyncAnnotatedMigrationSample.java:2"
    ```
=== "Coroutines"
    ```kotlin
    --8<-- "examples/mongo-coroutines/src/main/kotlin/com/coditory/sherlock/samples/mongo/coroutines/MongoKtAnnotatedMigrationSample.kt:2"
    ```
=== "Reactor"
    ```java
    --8<-- "examples/mongo-reactor/src/main/java/com/coditory/sherlock/samples/mongo/reactor/MongoReactorAnnotatedMigrationSample.java:2"
    ```
=== "RxJava"
    ```java
    --8<-- "examples/mongo-rxjava/src/main/java/com/coditory/sherlock/samples/mongo/rxjava/MongoRxAnnotatedMigrationSample.java:2"
    ```

## Functional migration

Below example uses MongoDB, but sherlock migrator is available for all [connectors](connectors/index.md).
=== "Sync"
    ```java
    --8<-- "examples/mongo-sync/src/main/java/com/coditory/sherlock/samples/mongo/sync/MongoSyncMigrationSample.java:2"
    ```
=== "Coroutines"
    ```kotlin
    --8<-- "examples/mongo-coroutines/src/main/kotlin/com/coditory/sherlock/samples/mongo/coroutines/MongoKtMigrationSample.kt:2"
    ```
=== "Reactor"
    ```java
    --8<-- "examples/mongo-reactor/src/main/java/com/coditory/sherlock/samples/mongo/reactor/MongoReactorMigrationSample.java:2"
    ```
=== "RxJava"
    ```java
    --8<-- "examples/mongo-rxjava/src/main/java/com/coditory/sherlock/samples/mongo/rxjava/MongoRxMigrationSample.java:2"
    ```
