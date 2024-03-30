# Sherlock Distributed Lock
[![Build](https://github.com/coditory/sherlock-distributed-lock/actions/workflows/build.yml/badge.svg)](https://github.com/coditory/sherlock-distributed-lock/actions/workflows/build.yml)
[![Coverage](https://codecov.io/gh/coditory/sherlock-distributed-lock/branch/main/graph/badge.svg)](https://codecov.io/gh/coditory/sherlock-distributed-lock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.sherlock/sherlock-api/badge.svg)](https://search.maven.org/search?q=com.coditory.sherlock)

> Java Distributed lock library with database migration capabilities

Look for details in the [documentation](https://coditory.github.io/sherlock-distributed-lock/).

- **[supports multiple databases](https://coditory.github.io/sherlock-distributed-lock/connectors/)** - [SQL databases](https://coditory.github.io/sherlock-distributed-lock/connectors/sql/), [MongoDB](https://coditory.github.io/sherlock-distributed-lock/connectors/mongo/) and more
- **[multiple types of locks](https://coditory.github.io/sherlock-distributed-lock/locks)** - multiple ways to acquire a lock: reentrant, single-entrant and more
- **[basic DB migration](https://coditory.github.io/sherlock-distributed-lock/migrator)** - provides database migration process based on locks. No need for another library.
- **[synchronous and reactive API](https://coditory.github.io/sherlock-distributed-lock/api)** - exposes synchronous and reactive API (supports synchronous calls, Reactor, RxJava, Kotlin coroutines)
- **minimal set of dependencies** - the main dependency is a database driver

## Sample usage

Add dependency to `build.gradle`:
```groovy
dependencies {
  implementation "com.coditory.sherlock:sherlock-mongo:$version"
}
```

Create synchronous MongoDB backed lock:
```java
// Get mongo collection
// You can also use other DB or reactive API
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/sherlock");
MongoCollection<Document> collection = mongoClient
    .getDatabase("sherlock")
    .getCollection("locks");

// Create sherlock
Sherlock sherlock = MongoSherlock.create(collection);

// Create a lock
DistributedLock lock = sherlock.createLock("sample-lock");

// Acquire a lock, run action and finally release the lock
lock.runLocked(() -> System.out.println("Lock granted!"));
```

