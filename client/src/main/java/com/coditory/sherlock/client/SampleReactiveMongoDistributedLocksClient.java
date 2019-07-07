package com.coditory.sherlock.client;

import com.coditory.sherlock.reactive.ReactiveDistributedLock;
import com.coditory.sherlock.reactive.ReactiveSherlock;
import com.coditory.sherlock.reactive.ReactiveMongoSherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

public class SampleReactiveMongoDistributedLocksClient {
  public static void main(String[] args) {
    new SampleReactiveMongoDistributedLocksClient().run();
  }

  ReactiveSherlock createLocks() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://loclhost:27017/" + database);
    return ReactiveMongoSherlock.builder()
        .withMongoClient(mongoClient)
        .withDatabaseName(database)
        .build();
  }

  void run() {
    ReactiveSherlock locks = createLocks();
    ReactiveDistributedLock lock = locks.createLock("sample-acquire");
    // ...
  }
}
