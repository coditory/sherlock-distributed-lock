package com.coditory.sherlock.client;

import com.coditory.sherlock.reactive.ReactiveMongoSherlock;
import com.coditory.sherlock.reactive.connector.LockResult;
import com.coditory.sherlock.reactor.ReactorDistributedLock;
import com.coditory.sherlock.reactor.ReactorSherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

public class ReactiveMongoSherlockSample {
  public static void main(String[] args) {
    new ReactiveMongoSherlockSample().run();
  }

  ReactorSherlock createSherlock() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://loclhost:27017/" + database);
    return ReactiveMongoSherlock.builder()
        .withMongoClient(mongoClient)
        .withDatabaseName(database)
        .build()
        .map(ReactorSherlock::reactorSherlock);
  }

  void run() {
    ReactorSherlock sherlock = createSherlock();
    ReactorDistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquire()
        .filter(LockResult::isLocked)
        .flatMap(result -> {
          System.out.println("Lock granted!");
          return lock.release();
        })
        .block();
  }
}
