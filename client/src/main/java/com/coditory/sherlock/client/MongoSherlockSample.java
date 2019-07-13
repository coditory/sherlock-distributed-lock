package com.coditory.sherlock.client;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.MongoSherlock;
import com.coditory.sherlock.Sherlock;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.time.Clock;
import java.time.Duration;

public class MongoSherlockSample {
  private static Sherlock createSherlock() {
    return createSherlock("localhost");
  }

  private static Sherlock createSherlock(String ownerId) {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
    return MongoSherlock.builder()
        .withMongoClient(mongoClient) // required
        .withDatabaseName(database) // required
        .withClock(Clock.systemDefaultZone()) // default: Clock.systemDefaultZone()
        .withCollectionName("locks") // default: "locks"
        .withLockDuration(Duration.ofMinutes(5)) // default: 5 min
        .withOwnerId("datacenter-X-machine-Y-instance-Z") // default: generated unique string
        .build();
  }

  public static void main(String... args) throws InterruptedException {
    Sherlock sherlock = createSherlock();
    DistributedLock simpleLock = sherlock.createLock("sample-acquire");
    DistributedLock reentrantLock = sherlock.createReentrantLock("sample-acquire");
    DistributedLock overridingLock = sherlock.createOverridingLock("sample-acquire");
    reentrantLock.acquire();
    Thread.sleep(60_000);
    reentrantLock.acquire();
  }

  public static void main2() {
    Sherlock sherlock = createSherlock();
    DistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquireAndExecute(() -> System.out.println("Lock granted!"));
  }
}
