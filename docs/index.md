# Sherlock <small>Distributed Lock</small>

[![Build Status](https://travis-ci.org/coditory/sherlock-distributed-lock.svg?branch=master)](https://travis-ci.org/coditory/sherlock-distributed-lock)
[![Coverage Status](https://coveralls.io/repos/github/coditory/sherlock-distributed-lock/badge.svg)](https://coveralls.io/github/coditory/sherlock-distributed-lock)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.sherlock/sherlock-api-sync/badge.svg)](https://search.maven.org/search?q=com.coditory.sherlock)
[![JavaDoc](https://www.javadoc.io/badge/com.coditory.sherlock/sherlock-api-sync.svg)](http://www.javadoc.io/doc/com.coditory.sherlock/sherlock-api-sync)

<div style="text-align: center">
<img src="assets/img/logo.png" alt="Sherlock Distributed Lock Logo">
</div>

[Sherlock](https://github.com/coditory/sherlock-distributed-lock) is a distributed locking library for JVM projects.
It exposes both synchronous and reactive APIs (Reactor, RxJava, Kotlin Coroutines)
and uses database [connectors](connectors) to store locks.
It was created as a simple solution to manage distributed locks among microservices.


## How it works?

Locks are acquired for a [specific duration](locks#lock-duration).
When lock owning instance unexpectedly goes down,
lock is automatically released after expiration.

## Quick start

- [MongoDB](connectors/mongo) - using sherlock with MongoDB
- [SQL](connectors/sql) - using sherlock with SQL databases
- [In-Memory](connectors/inmem) - using in-memory Sherlock for local development or testing
- [Testing](testing) - stubbing and mocking Sherlock in unit tests
- [Migrator](migrator) - using Sherlock as for database migration

