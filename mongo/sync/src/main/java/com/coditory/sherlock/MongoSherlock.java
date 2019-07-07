package com.coditory.sherlock;

import com.coditory.sherlock.common.InstanceId;
import com.coditory.sherlock.common.util.Preconditions;
import com.mongodb.client.MongoClient;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.common.DistributedLockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.common.DistributedLockDefaults.DEFAULT_DB_TABLE_NAME;
import static com.coditory.sherlock.common.DistributedLockDefaults.DEFAULT_INSTANCE_ID;
import static com.coditory.sherlock.common.DistributedLockDefaults.DEFAULT_LOCK_DURATION;

public class MongoSherlock {
  private MongoClient mongoClient;
  private String databaseName;
  private String collectionName = DEFAULT_DB_TABLE_NAME;
  private Duration duration = DEFAULT_LOCK_DURATION;
  private InstanceId instanceId = DEFAULT_INSTANCE_ID;
  private Clock clock = DEFAULT_CLOCK;

  public static MongoSherlock builder() {
    return new MongoSherlock();
  }

  private MongoSherlock() {
    // deliberately empty
  }

  public MongoSherlock withMongoClient(MongoClient mongoClient) {
    this.mongoClient = Preconditions.expectNonNull(mongoClient, "Expected non null mongoClient");
    return this;
  }

  public MongoSherlock withDatabaseName(String databaseName) {
    this.databaseName = Preconditions
        .expectNonEmpty(databaseName, "Expected non empty databaseName");
    return this;
  }

  public MongoSherlock withCollectionName(String collectionName) {
    this.collectionName = Preconditions
        .expectNonEmpty(collectionName, "Expected non empty collectionName");
    return this;
  }

  public MongoSherlock withLockDuration(Duration duration) {
    this.duration = Preconditions.expectNonNull(duration, "Expected non null duration");
    return this;
  }

  public MongoSherlock withServiceInstanceId(String instanceId) {
    this.instanceId = InstanceId.of(instanceId);
    return this;
  }

  public MongoSherlock withClock(Clock clock) {
    this.clock = Preconditions.expectNonNull(clock, "Expected non null clock");
    return this;
  }

  public Sherlock build() {
    Preconditions.expectNonNull(mongoClient, "Expected non null mongoClient");
    Preconditions.expectNonEmpty(databaseName, "Expected non empty databaseName");
    MongoDistributedLockDriver driver = new MongoDistributedLockDriver(
        mongoClient, databaseName, collectionName, clock);
    return new Sherlock(driver, instanceId, duration);
  }
}
