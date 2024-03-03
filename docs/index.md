# Sherlock <small>Distributed Lock</small>

[![Build Status](https://travis-ci.org/coditory/sherlock-distributed-lock.svg?branch=master)](https://travis-ci.org/coditory/sherlock-distributed-lock)
[![Coverage Status](https://coveralls.io/repos/github/coditory/sherlock-distributed-lock/badge.svg)](https://coveralls.io/github/coditory/sherlock-distributed-lock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.sherlock/sherlock-api-sync/badge.svg)](https://search.maven.org/search?q=com.coditory.sherlock)
[![JavaDoc](https://www.javadoc.io/badge/com.coditory.sherlock/sherlock-api-sync.svg)](http://www.javadoc.io/doc/com.coditory.sherlock/sherlock-api-sync)

<div style="text-align: center">
<img src="assets/img/logo.png" alt="Sherlock Distributed Lock Logo">
</div>

[Sherlock](https://github.com/coditory/sherlock-distributed-lock) is a distributed locking library for JVM projects.
It exposes both synchronous and reactive [APIs](api) (Reactor, RxJava, Kotlin Coroutines) and uses database [connectors](connectors) to store locks.
It was created as a simple solution to manage distributed locks among multiple microservices.

## How it works?

Locks are acquired for a [specific duration](locks#lock-duration).
When lock owning instance unexpectedly goes down,
lock is automatically released after expiration.

!!! important "Read and write from the same DB node"
    Make sure that DB connection passed to Sherlock reads and writes to the same DB node
    so every lock change is visible to all of your services.

## Quick start

Add dependency to `build.gradle.kts`:

```kotlin
dependencies {
  implementation("com.coditory.sherlock:sherlock-mongo:{{ version }}")
}
```

Create a lock:
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

...or acquire a lock in a more concise way:
```java
lock.acquireAndExecute(() -> {
  System.out.println("Lock granted!");
});
```

!!! info "Learn more"
    Learn how to use different [APIs](api) and [connectors](connectors).
