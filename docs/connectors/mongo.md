# Mongo Distributed Lock

MongoDB connector enables distributed locking on [MongoDB](https://www.mongodb.com/).
It was [tested on MongoDB v3.6]({{ vcs_baseurl }}/mongo/sync/src/integration/groovy/com/coditory/sherlock/MongoHolder.groovy).

!!! info "Mongo Client"
    There is no need for a special MongoClient configuration. Default settings, where all writes use master node, are sufficient.
    Sherlock uses no read queries and only the following modification operations:
    [`findOneAndReplace`](https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndReplace/),
    [`findOneAndDelete`](https://docs.mongodb.com/manual/reference/method/db.collection.findOneAndDelete/),
    [`deleteMany`](https://docs.mongodb.com/manual/reference/method/db.collection.deleteMany/).

## Usage

Add dependencies to `build.gradle.kts`:

=== "Sync"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-mongo:{{ version }}")
        implementation("org.mongodb:mongodb-driver-sync:$versions.mongodb")
    }
    ```
=== "Coroutines"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-mongo-coroutines:{{ version }}")
        implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$versions.mongodb")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versions.coroutines")
    }
    ```
=== "Reactor"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-mongo-reactor:{{ version }}")
        implementation("org.mongodb:mongodb-driver-reactivestreams:$versions.mongodb")
    }
    ```
=== "RxJava"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-mongo-rxjava:{{ version }}")
        implementation("org.mongodb:mongodb-driver-reactivestreams:$versions.mongodb")
    }
    ```

Create sherlock instance and distributed lock:
=== "Sync"
    ```java
    --8<-- "examples/mongo-sync/src/main/java/com/coditory/sherlock/samples/mongo/sync/MongoSyncLockSample.java:2"
    ```
=== "Coroutines"
    ```kotlin
    --8<-- "examples/mongo-coroutines/src/main/kotlin/com/coditory/sherlock/samples/mongo/coroutines/MongoKtLockSample.kt:2"
    ```
=== "Reactor"
    ```java
    --8<-- "examples/mongo-reactor/src/main/java/com/coditory/sherlock/samples/mongo/reactor/MongoReactorLockSample.java:2"
    ```
=== "RxJava"
    ```java
    --8<-- "examples/mongo-rxjava/src/main/java/com/coditory/sherlock/samples/mongo/rxjava/MongoRxLockSample.java:2"
    ```

!!! info "Learn more"
    See full source code example on  [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/example/).

## Configuration

Configuration is available via sherlock builder: 
=== "Sync"
    ```java
    MongoSherlock.builder()
        .withClock(Clock.systemUTC())
        .withLockDuration(Duration.ofMinutes(5))
        .withUniqueOwnerId()
        .withLocksCollection(getCollection())
        .build();
    ```
=== "Coroutines"
    ```kotlin
    MongoSherlock.builder()
        .withClock(Clock.systemUTC())
        .withLockDuration(Duration.ofMinutes(5))
        .withUniqueOwnerId()
        .withLocksCollection(getCollection())
        .build()
    ```
=== "Reactor"
    ```java
    MongoSherlock.builder()
        .withClock(Clock.systemUTC())
        .withLockDuration(Duration.ofMinutes(5))
        .withUniqueOwnerId()
        .withLocksCollection(getCollection())
        .build();
    ```
=== "RxJava"
    ```java
    MongoSherlock.builder()
        .withClock(Clock.systemUTC())
        .withLockDuration(Duration.ofMinutes(5))
        .withUniqueOwnerId()
        .withLocksCollection(getCollection())
        .build();
    ```

Parameters:

- `clock` (default: `Clock.systemUTC()`) - used to generate acquisition and expiration timestamps.
- `lockDuration` (default: `Duration.ofMinutes(5)`) - used a default lock expiration time.
  If lock is not released and expiration time passes, the lock automatically becomes released.
- `ownerId` (default: `UniqueOwnerId()`) - used to identify lock owner.
  There are different policies available for generating the ownerId.
- `locksCollection` - MongoDb collection used to store the locks.

## Locks collection
 
Sample lock document:

```json
{
  // Lock id
  "_id": "lock-id",
  // Owner id
  "acquiredBy": "aec5229a-1728-4200-b8d1-14f54ed9ac78",
  // Lock acquisition moment
  "acquiredAt": { "$date": "2024-03-20T08:03:02.231Z" },
  // Lock expiation time.
  // Might be null for locks that do not expire
  "expiresAt": { "$date": "2024-03-20T08:08:02.231Z" }
}
```