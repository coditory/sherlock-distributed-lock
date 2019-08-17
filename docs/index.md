# Sherlock <small>Distributed Lock</small>

[![Build Status](https://travis-ci.org/coditory/sherlock-distributed-lock.svg?branch=master)](https://travis-ci.org/coditory/sherlock-distributed-lock)
[![Coverage Status](https://coveralls.io/repos/github/coditory/sherlock-distributed-lock/badge.svg)](https://coveralls.io/github/coditory/sherlock-distributed-lock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.sherlock/sherlock-api-sync/badge.svg)](https://search.maven.org/search?q=com.coditory.sherlock)
[![JavaDoc](http://www.javadoc.io/badge/com.coditory.sherlock/sherlock-api-sync.svg)](http://www.javadoc.io/doc/com.coditory.sherlock/sherlock-api-sync)
[![Join the chat at https://gitter.im/coditory/sherlock-distributed-lock](https://badges.gitter.im/coditory/sherlock-distributed-lock.svg)](https://gitter.im/coditory/sherlock-distributed-lock?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

<div style="text-align: center">
<img src="assets/img/logo.png" alt="Sherlock Distributed Lock Logo">
</div>

## Distributed lock JVM library 

[Sherlock Distributed Lock](https://github.com/coditory/sherlock-distributed-lock) is a JVM library 
that provides distributed locking mechanism. It uses a database to store locks and exposes both synchronous and reactive API.
It was created as a simple solution to manage distributed locks among multiple microservices.

<small>Name Sherlock comes from words "shared lock".</small>

## The problem
Distribute locking may be achieved through multiple mechanisms:

- Locking with service discovery like Zookeeper
- Locking in SQL transaction
- Locking with atomic find and modify database actions

This library was designed to implement the last option. Lock is acquired with an atomic update.
To solve the problem of not released lock (because of a lock owner failure) locks are acquired for a specific duration.
When time passes, lock is expired and is automatically released.

## Quick start

Add dependency to `build.gradle`:
```gradle
dependencies {
  compile "com.coditory.sherlock:sherlock-mongo-sync:{{ version }}"
}
```

Create synchronous lock:
```java
// Get mongo locks collection
String database = "sherlock";
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
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
    Learn hot use different [APIs](api.md) and [connectors](connectors.md).
