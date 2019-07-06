package com.coditory.distributed.lock.client;

import com.coditory.distributed.lock.mongo.reactive.ReactiveMongoDistributedLockDriver;
import com.coditory.distributed.lock.reactive.ReactiveDistributedLock;
import com.coditory.distributed.lock.reactive.ReactiveDistributedLocks;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import static java.time.Clock.systemDefaultZone;

public class SampleReactiveMongoDistributedLocksClient {
  public static void main(String[] args) {
    new SampleReactiveMongoDistributedLocksClient().run();
  }

  ReactiveDistributedLocks createLocks() {
    String database = "distributed-locks";
    MongoClient mongoClient = MongoClients.create("mongodb://loclhost:27017/" + database);
    ReactiveMongoDistributedLockDriver driver = new ReactiveMongoDistributedLockDriver(
        mongoClient, database, "locks", systemDefaultZone());
    return ReactiveDistributedLocks.builder(driver)
        .withServiceInstanceId("my-machine")
        .build();
  }

  void run() {
    ReactiveDistributedLocks locks = createLocks();
    ReactiveDistributedLock lock = locks.createLock("sample-lock");
    // ...
  }
}
