# Sherlock Distributed Lock
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.sherlock/sherlock-sync/badge.svg)](https://search.maven.org/search?q=com.coditory.sherlock)
[![Build Status](https://travis-ci.org/coditory/sherlock-distributed-lock.svg?branch=master)](https://travis-ci.org/coditory/sherlock-distributed-lock)
[![Coverage Status](https://coveralls.io/repos/github/coditory/sherlock-distributed-lock/badge.svg)](https://coveralls.io/github/coditory/sherlock-distributed-lock)
[![Join the chat at https://gitter.im/coditory/sherlock-distributed-lock](https://badges.gitter.im/coditory/sherlock-distributed-lock.svg)](https://gitter.im/coditory/sherlock-distributed-lock?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**Small and single purpose** distributed locking library for JVM. Provides multiple implementations (over single abstraction) for distributed locking:
- [mongo-synchronous](./mongo/sync)
- [mongo-reactive](./mongo/reactive)
- ...postgres implementation comes next

Before using the library read [main problems of distributed locking](#disclaimer).

## Basic usage

### Synchronous usage

Add dependency to `build.gradle`:

```gradle
dependencies {
  compile "sherlock-mongo-sync:0.1.4"
}
```

Create synchronous lock:
```java
// Initialize Sherlock
String database = "sherlock";
MongoClient mongoClient = MongoClients.create("mongodb://loclhost:27017/" + database);
Sherlock sherlock = MongoSherlock.builder()
  .withMongoClient(mongoClient) // required
  .withDatabaseName(database) // required
  .withClock(Clock.systemDefaultZone()) // default: Clock.systemDefaultZone()
  .withCollectionName("locks") // default: "locks"
  .withLockDuration(Duration.ofMinutes(5)) // default: 5 min
  .withServiceInstanceId("datacenter-X-machine-Y-instance-Z") // default: generated unique string
  .build();
// Create a lock
DistributedLock lock = sherlock.createLock("sample-lock");
```

Acquire a lock:
```java
// Acquire a lock
if (lock.acquire()) {
  try {
    System.out.println("Lock granted!");
  } finally {
    lock.release();
  }
}
```

...or acquire a lock in a less verbose way:
```java
lock.acquireAndExecute(() -> {
  System.out.println("Lock granted!");
});
```

### Reactive usage

Add dependency to `build.gradle`:

```gradle
dependencies {
  compile "sherlock-mongo-reactive:0.1.0"
  compile "sherlock-reactor:0.1.0"
}
```

Create synchronous lock:
```java
// Initialize Sherlock
String database = "sherlock";
MongoClient mongoClient = MongoClients.create("mongodb://loclhost:27017/" + database);
ReactorSherlock sherlock = ReactiveMongoSherlock.builder()
  .withMongoClient(mongoClient)
  .withDatabaseName(database)
  .build()
  .map(ReactorSherlock::reactorSherlock);
// Create a lock
ReactorDistributedLock lock = sherlock.createLock("sample-lock");
```

Acquire a lock:
```java
// Acquire a lock
lock.acquire()
  .filter(LockResult::isLocked)
  .flatMap(result -> {
    System.out.println("Lock granted!");
    return lock.release();
  })
  .block();
```

...or shorter
```java
lock.acquireAndExecute(() -> Mono.just("Lock granted!"))
  .block();
```

## Lock duration

There are 3 methods for acquiring a lock:
- `lock.acquire()` - acquires lock for a default duration (5 minutes) after which lock is automatically released
- `lock.acquire(Duration.ofMinutes(3))` - acquires a lock for a specific duration
- `lock.acquireForever()` - acquires a lock forever. Use it wisely.

## Lock types

There are 3 types of locks:
- `SingleEntrantDistributedLock` - lock owner cannot acquire the same lock for the second time
- `ReentrantDistributedLock` - lock owner can acquire the same lock multiple times
- `OverridingDistributedLock` - lock that acquires and releases the lock with `sudo` rights

`SingleEntrantDistributedLock` and `ReentrantDistributedLock` handles the problem of [releasing a lock](#disclaimer) by using an expiration mechanism.

`OverridingDistributedLock` was created for purely administrative tasks.

### Acquiring a lock by different lock types

`SingleEntrantDistributedLock`
```java
DistributedLock lock = sherlock.createLock("single-entrant");
Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == false
assert lock.acquire() == false;|
```

`ReentrantDistributedLock`
```java
DistributedLock lock = sherlock.createReentrantLock("reentrant");
Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == false
assert lock.acquire() == true; |
```

`OverridingDistributedLock`
```java
DistributedLock lock = sherlock.createOverridingLock("overriding");
Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == true
assert lock.acquire() == true; |
assert lock.acquire() == true; |
```

### Releasing a lock by different lock types

`SingleEntrantDistributedLock` and `ReentrantDistributedLock`
```java
Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == false
                               | assert lock.release() == false
assert lock.release() == true; |
assert lock.release() == false;|
```

`OverridingDistributedLock`
```java
Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == true
                               | assert lock.release() == true
assert lock.release() == false;|
assert lock.release() == false;|
```

## Lock state in storage
In mongo distributed lock implementation, lock is stored as a document:

```json
{
  "_id": "lock-id",
  "acquiredBy": "service-instance-id",
  "acquiredAt": { "$date": 1562502838189 },
  "expiresAt": { "$date": 1562503458189 }
}
```

## Testability

For easy stubbing and mocking all exposed
api uses interfaces (see: `Sherlock`, `ReactorSherlock`, `DistributedLock`, `ReactorDistributedLock`).

Exposed interfaces have already stubs or mocks prepared. See:
`SherlockStub`, `ReactorSherlockStub`, `DistributedLockMock` and `ReactorDistributedLockMock`.

Sample usage in spock tests:

```groovy
def "should release a lock after operation"() {
  given: "there is a released lock"
    DistributedLockMock lock = DistributedLockMock.alwaysOpenedLock()
  when: "single instance action is executed"
    boolean taskPerformed = singleInstanceAction(lock)
  then: "the task was performed"
    taskPerformed == true
  and: "action released the lock"
    lock.wasReleased == true
}

def "should not perform single instance action when lock is locked"() {
  given: "there is a lock acquired by other instance"
    DistributedLockMock lock = DistributedLockMock.alwaysClosedLock()
  when: "single instance action is executed"
    boolean taskPerformed = singleInstanceAction(lock)
  then: "action did not enter perform the task"
    taskPerformed == false
  and: "action tried to acquire the lock"
    lock.wasAcquired == true
  and: "action did not release the lock"
    lock.wasReleased == false
}
```

## Disclaimer

#### How to ensure that an acquired lock is released?

It is possible the an instance that acquired a lock may go down before releasing it.

```java
if (lock.acquire()) {
  try {
    System.out.println("Lock granted!");
    System.exit(); // ...or OOM or any other cause
  } finally {
    // if this part is not reached
    // lock will be released after exipration time
    lock.release();
  }
}
```

This problem is fixed by [automatically releasing a lock](#lock-duration) after expiration time.

#### How to ensure that an operation did not exceed a lock duration?**
Because of stop-the-world (...or multiple other causes) an operation that required a lock may
take longer to finish than the lock duration. This problem is not trivial
and well described by [Martin Kleppmann](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html).
This library is not designed to solve it.
Simply make the lock duration as long as possible and don't use it in a per request manner.

## License
**[sherlock-distributed-lock](#sherlock-distributed-lock)** is published under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
