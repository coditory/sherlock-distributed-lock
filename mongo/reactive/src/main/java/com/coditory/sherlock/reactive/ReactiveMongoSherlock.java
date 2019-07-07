package com.coditory.sherlock.reactive;

import com.coditory.sherlock.common.InstanceId;
import com.mongodb.reactivestreams.client.MongoClient;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.common.DistributedLockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.common.DistributedLockDefaults.DEFAULT_DB_TABLE_NAME;
import static com.coditory.sherlock.common.DistributedLockDefaults.DEFAULT_INSTANCE_ID;
import static com.coditory.sherlock.common.DistributedLockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

public class ReactiveMongoSherlock {
  private MongoClient mongoClient;
  private String databaseName;
  private String collectionName = DEFAULT_DB_TABLE_NAME;
  private Duration duration = DEFAULT_LOCK_DURATION;
  private InstanceId instanceId = DEFAULT_INSTANCE_ID;
  private Clock clock = DEFAULT_CLOCK;

  public static ReactiveMongoSherlock builder() {
    return new ReactiveMongoSherlock();
  }

  private ReactiveMongoSherlock() {
    // deliberately empty
  }

  public ReactiveMongoSherlock withMongoClient(MongoClient mongoClient) {
    this.mongoClient = expectNonNull(mongoClient, "Expected non null mongoClient");
    return this;
  }

  public ReactiveMongoSherlock withDatabaseName(String databaseName) {
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    return this;
  }

  public ReactiveMongoSherlock withCollectionName(String collectionName) {
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    return this;
  }

  public ReactiveMongoSherlock withLockDuration(Duration duration) {
    this.duration = expectNonNull(duration, "Expected non null duration");
    return this;
  }

  public ReactiveMongoSherlock withServiceInstanceId(String instanceId) {
    this.instanceId = InstanceId.of(instanceId);
    return this;
  }

  public ReactiveMongoSherlock withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  public ReactiveSherlock build() {
    expectNonNull(mongoClient, "Expected non null mongoClient");
    expectNonEmpty(databaseName, "Expected non empty databaseName");
    ReactiveMongoDistributedLockDriver driver = new ReactiveMongoDistributedLockDriver(
        mongoClient, databaseName, collectionName, clock);
    return new ReactiveSherlock(driver, instanceId, duration);
  }
}
