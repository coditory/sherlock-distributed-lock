# Sherlock - Distributed Lock
*Distributed lock library for JVM*

[![Join the chat at https://gitter.im/coditory/sherlock-distributed-lock](https://badges.gitter.im/coditory/sherlock-distributed-lock.svg)](https://gitter.im/coditory/sherlock-distributed-lock?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/coditory/sherlock-distributed-lock.svg?branch=master)](https://travis-ci.org/coditory/sherlock-distributed-lock)
[![Coverage Status](https://coveralls.io/repos/github/coditory/sherlock-distributed-lock/badge.svg)](https://coveralls.io/github/coditory/sherlock-distributed-lock)

Distributed lock library for JVM.
Provides distributed locking abstraction and multiple implementations:
- [mongo-synchronous](./mongo/sync)
- [mongo-reactive](./mongo/reactive)
- ...postgres implementation comes next

Before using the library make sure to know [main problems of distributed locking](#disclaimer).

## Sample usage

### Synchronous usage

Add dependency to `build.gradle`:

```gradle
dependencies {
  compile "sherlock-mongo-sync:0.1.0"
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

There are 3 ways to acquire a lock:
- `lock.acquire()` - acquires lock for a default duration after which lock is automatically released
- `lock.acquire(Duration.ofMinutes(3))` - acquires a lock for a custom duration
- `lock.acquireForever()` - acquires a lock forever. Use it wisely.

Instance that acquired a lock may go down before releasing it.
That is why by default locks are released after some time.

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


## Lock types

There are 3 types of locks:
- `SingleEntrantDistributedLock` - lock owner cannot acquire the lock more than once
- `ReentrantDistributedLock` - lock owner can acquire the lock multiple times
- `OverridingDistributedLock` - administrative lock that changes lock state with `sudo` rights

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

### Releasing by different lock types

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
Using mongo distributed lock implementation, acquired lock is stored a document:

```json
{
  "_id": "lock-id",
  "acquiredBy": "service-instance-id",
  "acquiredAt": { "$date": 1562502838189 },
  "expiresAt": { "$date": 1562503458189 }
}
```

## Testability

For easy stubbing and mocking all most important
types (like `Sherlock`, `ReactorSherlock`, `DistributedLock`, `ReactorDistributedLock`) are interfaces.

Moreover all of them have stubs or mocks ready to be used. See:
`SherlockStub`, `ReactorSherlockStub`, `DistributedLockMock` and `ReactorDistributedLockMock`.

Sample usage in spock test:

```groovy
def "should release a lock after operation"() {
  given: "there is an lock ready to be acquired"
    DistributedLockMock lock = DistributedLockMock.alwaysOpenedLock()
  when: "single instance action is executed"
    boolean executed = singleInstanceAction(lock)
  then: "action was executed"
    executed == true
  and: "action released the lock when finished"
    lock.wasReleased == true
}

def "should not perform single instance action when lock is locked"() {
  given: "there is an lock acquired by other instance"
    DistributedLockMock lock = DistributedLockMock.alwaysClosedLock()
  when: "single instance action is executed"
    boolean executed = singleInstanceAction(lock)
  then: "action was not executed"
    executed == false
}
```

## Disclaimer

#### How to ensure that an acquired lock is released?
This problem is fixed by [automatically releasing a lock](#lock-duration) after a some time.

#### How to ensure that an operation did not exceed a lock duration?**
Because of stop-the-world (...or multiple other causes) an operation that required a lock may
take longer to finish than the lock duration. This problem is not trivial
and well described by [Martin Kleppmann](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html).
This library is not designed to solve it.
Simply make the lock duration as long as possible and don't use it in a per request manner.

## License
**sherlock-distributed-lock** is published under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
