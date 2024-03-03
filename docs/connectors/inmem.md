# In Memory Distributed Lock

In Memory connector was created for local development and testing purposes.

## Usage
Add dependency to `build.gradle.kts`:

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

Create and acquire a lock:
=== "Sync"
    ```java
    Sherlock sherlock = inMemorySherlockBuilder()
        .withClock(Clock.systemUTC())
        .withUniqueOwnerId()
        .withSharedStorage()
        .build();
    // ...or short equivalent:
    // Sherlock sherlockWithDefaults = inMemorySherlock();
    DistributedLock lock = sherlock.createLock("sample-lock");
    lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    ```
=== "Coroutines"
    ```kotlin
    val sherlock = coroutineInMemorySherlockBuilder()
        .withClock(Clock.systemUTC())
        .withUniqueOwnerId()
        .withSharedStorage()
        .build()
    // ...or short equivalent:
    // val sherlockWithDefaults = coroutineInMemorySherlock()
    val lock = sherlock.createLock("sample-lock")
    lock.acquireAndExecute { logger.info("Lock acquired!") }
    ```
=== "Reactor"
    ```java
    ReactorSherlock sherlock = reactorInMemorySherlockBuilder()
        .withClock(Clock.systemUTC())
        .withUniqueOwnerId()
        .withSharedStorage()
        .build();
    // ...or short equivalent:
    // ReactorSherlock sherlockWithDefaults = reactorInMemorySherlock();
    ReactorDistributedLock lock = sherlock.createLock("sample-lock");
    lock.acquireAndExecute(Mono.fromCallable(() -> {
        logger.info("Lock acquired!");
        return true;
    })).block();
    ```
=== "RxJava"
    ```java
    RxSherlock sherlock = rxInMemorySherlockBuilder()
        .withClock(Clock.systemUTC())
        .withUniqueOwnerId()
        .withSharedStorage()
        .build();
    // ...or short equivalent:
    // RxSherlock sherlockWithDefaults = rxInMemorySherlock();
    RxDistributedLock lock = sherlock.createLock("sample-lock");
    lock.acquireAndExecute(Single.fromCallable(() -> {
        logger.info("Lock acquired!");
        return true;
    })).blockingGet();
    ```

!!! info "Learn more"
    See the full [examples]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/samples),
    or read sherlock builder [javadoc](https://www.javadoc.io/page/com.coditory.sherlock/sherlock-sql/latest/com/coditory/sherlock/InMemorySherlockBuilder.html).

## Parameters

In-memory sherlock parameters exposed in the builder:

- `clock` - clock used to generate lock creation and expiration time.
- `ownerId` - defines lock owner unique identifier. This is the value that is used to distinguish lock owners. 
If two processes use the same owner id they are acquire the same [reentrant lock](../locks/#reentrantdistributedlock).
- `sharedStorage` - use shared storage for all in-mem locks.