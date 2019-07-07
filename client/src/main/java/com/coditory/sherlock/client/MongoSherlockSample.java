package com.coditory.sherlock.client;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.MongoSherlock;
import com.coditory.sherlock.Sherlock;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.time.Clock;
import java.time.Duration;

public class MongoSherlockSample {
  public static void main(String[] args) {
    new MongoSherlockSample().run();
  }

  Sherlock createSherlock() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://loclhost:27017/" + database);
    return MongoSherlock.builder()
        .withMongoClient(mongoClient)
        .withDatabaseName(database)
        .withClock(Clock.systemDefaultZone()) // default: Clock.systemDefaultZone()
        .withCollectionName("locks") // default: "locks"
        .withLockDuration(Duration.ofMinutes(3)) // default: 5 min
        .withServiceInstanceId("datacenter-X-machine-Y-instance-Z") // default: generated unique string
        .build();
  }

  void run() {
    Sherlock sherlock = createSherlock();
    DistributedLock lock = sherlock.createLock("sample-acquire");
    if (lock.acquire()) {
      System.out.println("Lock granted!");
    }
  }

  void run2() {
    Sherlock sherlock = createSherlock();
    DistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquireAndExecute(() -> {
      System.out.println("Lock granted!");
    });
  }
}
