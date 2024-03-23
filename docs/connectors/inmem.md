# In Memory Distributed Lock

The in-memory connector was created for local development and testing purposes.

## Usage
Add dependencies to `build.gradle.kts`:

=== "Sync"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-inmem:{{ version }}")
    }
    ```
=== "Coroutines"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-inmem-coroutine:{{ version }}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versions.coroutines")
    }
    ```
=== "Reactor"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-inmem-reactor:{{ version }}")
    }
    ```
=== "RxJava"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-inmem-rxjava:{{ version }}")
    }
    ```

Create sherlock instance and distributed lock:
=== "Sync"
    ```java
    --8<-- "examples/inmem-sync/src/main/java/com/coditory/sherlock/samples/inmem/sync/InMemSyncLockSample.java:2"
    ```
=== "Coroutines"
    ```kotlin
    --8<-- "examples/inmem-coroutines/src/main/kotlin/com/coditory/sherlock/samples/inmem/coroutines/InMemKtLockSample.kt:2"
    ```
=== "Reactor"
    ```java
    --8<-- "examples/inmem-reactor/src/main/java/com/coditory/sherlock/samples/inmem/reactor/InMemReactorLockSample.java:2"
    ```
=== "RxJava"
    ```java
    --8<-- "examples/inmem-rxjava/src/main/java/com/coditory/sherlock/samples/inmem/rxjava/InMemRxLockSample.java:2"
    ```

!!! info "Learn more"
    See full source code example on  [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/example/).

## Configuration

Configuration is available via sherlock builder:
=== "Sync"
```java
```
=== "Coroutines"
```kotlin
```
=== "Reactor"
```java
```
=== "RxJava"
```java
```

Parameters:

- `clock` (default: `Clock.systemUTC()`) - used to generate acquisition and expiration timestamps.
- `lockDuration` (default: `Duration.ofMinutes(5)`) - used a default lock expiration time.
  If lock is not released and expiration time passes, the lock automatically becomes released.
- `ownerId` (default: `UniqueOwnerId()`) - used to identify lock owner.
  There are different policies available for generating the ownerId.
- `locksCollection` - MongoDb collection used to store the locks.
- `sharedStorage` - use shared storage for all in-mem locks.