package com.coditory.sherlock.reactor.base

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import groovy.transform.CompileStatic
import org.testcontainers.containers.GenericContainer

@CompileStatic
class MongoInitializer {
  static final String databaseName = "distributed-acquire-mongo"
  static final MongoClient mongoClient = startMongo()

  private static MongoClient startMongo() {
    // using an older version to preserve backward compatibility
    GenericContainer mongo = new GenericContainer<>("mongo:3.4")
        .withExposedPorts(27017)
    mongo.start()
    return MongoClients.create("mongodb://localhost:${mongo.firstMappedPort}/$databaseName")
  }
}
