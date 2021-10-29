# Sherlock Distributed Lock
[![Build Status](https://travis-ci.com/coditory/sherlock-distributed-lock.svg?branch=master)](https://travis-ci.com/coditory/sherlock-distributed-lock)
[![Coverage Status](https://coveralls.io/repos/github/coditory/sherlock-distributed-lock/badge.svg)](https://coveralls.io/github/coditory/sherlock-distributed-lock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.sherlock/sherlock-api-sync/badge.svg)](https://search.maven.org/search?q=com.coditory.sherlock)
[![JavaDoc](http://www.javadoc.io/badge/com.coditory.sherlock/sherlock-api-sync.svg)](http://www.javadoc.io/doc/com.coditory.sherlock/sherlock-api-sync)

> Distributed lock library for JVM projects with database migration capabilities

Look for details in the [documentation](https://coditory.github.io/sherlock-distributed-lock/).

- **minimal dependency** - the main dependency is database driver that is probably already used in your project
- **provides different [types of locks](https://coditory.github.io/sherlock-distributed-lock/locs)** - small but flexible
- **provides [migration capabilities](https://coditory.github.io/sherlock-distributed-lock/migrator)** - no need for another library for database migration process
- **exposes [synchronous and reactive API](https://coditory.github.io/sherlock-distributed-lock/api)** - changing API should not be a revolution

## Sample usage

Add dependency to `build.gradle`:
```groovy
dependencies {
  compile "com.coditory.sherlock:sherlock-mongo-sync:1.4.12"
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

