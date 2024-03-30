# Sherlock <small>Distributed Lock</small>

[![Build](https://github.com/coditory/sherlock-distributed-lock/actions/workflows/build.yml/badge.svg)](https://github.com/coditory/sherlock-distributed-lock/actions/workflows/build.yml)
[![Coverage](https://codecov.io/gh/coditory/sherlock-distributed-lock/branch/main/graph/badge.svg)](https://codecov.io/gh/coditory/sherlock-distributed-lock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.sherlock/sherlock-api/badge.svg)](https://search.maven.org/search?q=com.coditory.sherlock)
[![JavaDoc](https://www.javadoc.io/badge/com.coditory.sherlock/sherlock-api.svg)](http://www.javadoc.io/doc/com.coditory.sherlock/sherlock-api)

<div style="text-align: center">
<img src="assets/img/logo.png" alt="Sherlock Distributed Lock Logo">
</div>

[Sherlock](https://github.com/coditory/sherlock-distributed-lock) is a distributed locking library for JVM projects.
It exposes both synchronous and reactive APIs (Reactor, RxJava, Kotlin Coroutines)
and uses database [connectors](connectors/index.md) to store locks.
It was created as a simple solution to manage distributed locks among microservices.


## How it works?

Locks are acquired for a [specific duration](locks.md#lock-duration).
When lock owning instance unexpectedly goes down,
lock is automatically released after expiration.

## Quick start

- [MongoDB](connectors/mongo.md) - using sherlock with MongoDB
- [SQL](connectors/sql.md) - using sherlock with SQL databases
- [In-Memory](connectors/inmem.md) - using in-memory Sherlock for local development or testing
- [Testing](testing.md) - stubbing and mocking Sherlock in unit tests
- [Migrator](migrator.md) - using Sherlock as for database migration

