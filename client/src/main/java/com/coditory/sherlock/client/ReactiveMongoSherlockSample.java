package com.coditory.sherlock.client;

import com.coditory.sherlock.reactive.ReactiveMongoSherlock;
import com.coditory.sherlock.reactive.ReactorDistributedLock;
import com.coditory.sherlock.reactive.ReactorSherlock;
import com.coditory.sherlock.reactive.driver.LockResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

public class ReactiveMongoSherlockSample {
  public static void main(String[] args) {
    new ReactiveMongoSherlockSample().run();
  }

  ReactorSherlock createSharelock() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://loclhost:27017/" + database);
    return ReactiveMongoSherlock.builder()
        .withMongoClient(mongoClient)
        .withDatabaseName(database)
        .build()
        .map(ReactorSherlock::reactorSherlock);
  }

  void run() {
    ReactorSherlock sharelock = createSharelock();
    ReactorDistributedLock lock = sharelock.createLock("sample-acquire");
    lock.acquire()
        .filter(LockResult::isLocked)
        .flatMap(result -> {
          System.out.println("Lock granted!");
          return lock.release();
        })
        .block();
  }
}
