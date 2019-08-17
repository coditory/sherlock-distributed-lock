package com.coditory.sherlock.sample;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.MongoSherlockBuilder.mongoSherlock;

public class MongoSyncSample {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private MongoCollection<Document> locksCollection() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
    return mongoClient
      .getDatabase("sherlock")
      .getCollection("locks");
  }

  void sampleMongoSherlock() {
    Sherlock sherlock = mongoSherlock()
      .withClock(Clock.systemDefaultZone())
      .withLockDuration(Duration.ofMinutes(5))
      .withUniqueOwnerId()
      .withLocksCollection(locksCollection())
      .build();
    // ...or simply
    // Sherlock sherlockWithDefaults = mongoSherlock(locksCollection());
    DistributedLock lock = sherlock.createLock("sample-lock");
    lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
  }

  public static void main(String[] args) {
    new MongoSyncSample().sampleMongoSherlock();
  }
}
