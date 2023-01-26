# Sherlock Distributed Lock
[![Build](https://github.com/coditory/sherlock-distributed-lock/actions/workflows/build.yml/badge.svg)](https://github.com/coditory/sherlock-distributed-lock/actions/workflows/build.yml)
[![Coverage](https://codecov.io/gh/coditory/sherlock-distributed-lock/branch/master/graph/badge.svg)](https://codecov.io/gh/coditory/sherlock-distributed-lock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.sherlock/sherlock-api-sync/badge.svg)](https://search.maven.org/search?q=com.coditory.sherlock)

> Java Distributed lock library with database migration capabilities

Look for details in the [documentation](https://coditory.github.io/sherlock-distributed-lock/).

- **minimal set of dependencies** - the main dependency is a database driver
- **[multiple types of locks](https://coditory.github.io/sherlock-distributed-lock/locks)** - multiple ways to acquire a lock
- **[basic DB migration](https://coditory.github.io/sherlock-distributed-lock/migrator)** - basic database migration process using locks, no need for another library
- **[synchronous and reactive API](https://coditory.github.io/sherlock-distributed-lock/api)** - exposes synchronous and reactive API (supports Reactor and RxJava)

## Sample usage

Add dependency to `build.gradle`:
```groovy
dependencies {
  implementation "com.coditory.sherlock:sherlock-mongo-sync:0.4.19"
}
```

Create synchronous MongoDB backed lock:
```java
// Get mongo locks collection
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/sherlock");
MongoCollection<Document> collection = mongoClient
    .getDatabase("sherlock")
    .getCollection("locks");

// Create sherlock
Sherlock sherlock = MongoSherlock.builder()
    .withLocksCollection(collection)
    .build();

// Create a lock
DistributedLock lock = sherlock.createLock("sample-lock");

// Acquire a lock
lock.acquireAndExecute(() -> {
    System.out.println("Lock granted!");
});
```

