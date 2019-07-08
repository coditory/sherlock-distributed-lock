package com.coditory.sherlock;

import com.coditory.sherlock.common.InstanceId;
import com.mongodb.client.MongoClient;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_DB_TABLE_NAME;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_INSTANCE_ID;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

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
    this.mongoClient = expectNonNull(mongoClient, "Expected non null mongoClient");
    return this;
  }

  public MongoSherlock withDatabaseName(String databaseName) {
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    return this;
  }

  public MongoSherlock withCollectionName(String collectionName) {
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    return this;
  }

  public MongoSherlock withLockDuration(Duration duration) {
    this.duration = expectNonNull(duration, "Expected non null duration");
    return this;
  }

  public MongoSherlock withServiceInstanceId(String instanceId) {
    this.instanceId = InstanceId.of(instanceId);
    return this;
  }

  public MongoSherlock withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  public Sherlock build() {
    expectNonNull(mongoClient, "Expected non null mongoClient");
    expectNonEmpty(databaseName, "Expected non empty databaseName");
    MongoDistributedLockDriver driver = new MongoDistributedLockDriver(
        mongoClient, databaseName, collectionName, clock);
    return new SherlockWithDriver(driver, instanceId, duration);
  }
}
