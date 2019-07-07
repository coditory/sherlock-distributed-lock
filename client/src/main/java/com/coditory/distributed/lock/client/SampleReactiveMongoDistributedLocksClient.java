package com.coditory.distributed.lock.client;

import com.coditory.distributed.lock.reactive.ReactiveDistributedLock;
import com.coditory.distributed.lock.reactive.ReactiveDistributedLocks;
import com.coditory.distributed.lock.reactive.ReactiveMongoDistributedLocks;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

public class SampleReactiveMongoDistributedLocksClient {
  public static void main(String[] args) {
    new SampleReactiveMongoDistributedLocksClient().run();
  }

  ReactiveDistributedLocks createLocks() {
    String database = "distributed-locks";
    MongoClient mongoClient = MongoClients.create("mongodb://loclhost:27017/" + database);
    return ReactiveMongoDistributedLocks.builder()
        .withMongoClient(mongoClient)
        .withDatabaseName(database)
        .build();
  }

  void run() {
    ReactiveDistributedLocks locks = createLocks();
    ReactiveDistributedLock lock = locks.createLock("sample-acquire");
    // ...
  }
}
