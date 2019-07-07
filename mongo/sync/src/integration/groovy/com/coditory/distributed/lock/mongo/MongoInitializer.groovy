package com.coditory.distributed.lock.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.testcontainers.containers.GenericContainer

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
