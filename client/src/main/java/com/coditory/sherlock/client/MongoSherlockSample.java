package com.coditory.sherlock.client;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.MongoSherlock;
import com.coditory.sherlock.Sherlock;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

public class MongoSherlockSample {
  private static Logger logger = LoggerFactory.getLogger(MongoSherlockSample.class);

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

  public static void main(String... args) {
    Sherlock sherlock = createSherlock();
    DistributedLock simpleLock = sherlock.createLock("sample-acquire");
    DistributedLock reentrantLock = sherlock.createReentrantLock("sample-acquire");
    DistributedLock overridingLock = sherlock.createOverridingLock("sample-acquire");
    simpleLock.acquire();
    simpleLock.acquire();
    reentrantLock.acquire();
    reentrantLock.acquire();
    overridingLock.acquire();
    overridingLock.acquire();

    simpleLock.release();
    simpleLock.release();
    reentrantLock.release();
    reentrantLock.release();
    overridingLock.release();
    overridingLock.release();
  }

  public static void main2() {
    Sherlock sherlock = createSherlock();
    DistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquireAndExecute(() -> System.out.println("Lock granted!"));
  }
}
