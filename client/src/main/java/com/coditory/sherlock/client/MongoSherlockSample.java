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
        .withMongoClient(mongoClient)
        .withDatabaseName(database)
        .withClock(Clock.systemDefaultZone())
        .withCollectionName("locks")
        .withLockDuration(Duration.ofMinutes(3))
        .withOwnerId(ownerId)
        .build();
  }

  public static void main() {
    Sherlock sherlock = createSherlock();
    DistributedLock lock = sherlock.createLock("sample-acquire");
    if (lock.acquire()) {
      System.out.println("Lock granted!");
    }
    System.out.println("release1: " + lock.release());
    System.out.println("release2: " + lock.release());
  }

  public static void main2() {
    Sherlock sherlock = createSherlock();
    DistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquireAndExecute(() -> System.out.println("Lock granted!"));
  }
}
