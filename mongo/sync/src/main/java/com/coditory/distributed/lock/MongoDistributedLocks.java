package com.coditory.distributed.lock;

import com.coditory.distributed.lock.common.InstanceId;
import com.mongodb.client.MongoClient;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.distributed.lock.common.DistributedLockDefaults.DEFAULT_INSTANCE_ID;
import static com.coditory.distributed.lock.common.DistributedLockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;
import static java.time.Clock.systemDefaultZone;

public class MongoDistributedLocks {
  private MongoClient mongoClient;
  private String databaseName;
  private String collectionName = "locks";
  private Duration duration = DEFAULT_LOCK_DURATION;
  private InstanceId instanceId = DEFAULT_INSTANCE_ID;
  private Clock clock = systemDefaultZone();

  public static MongoDistributedLocks builder() {
    return new MongoDistributedLocks();
  }

  private MongoDistributedLocks() {
    // deliberately empty
  }

  public MongoDistributedLocks withMongoClient(MongoClient mongoClient) {
    this.mongoClient = expectNonNull(mongoClient, "Expected non null mongoClient");
    return this;
  }

  public MongoDistributedLocks withDatabaseName(String databaseName) {
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    return this;
  }

  public MongoDistributedLocks withCollectionName(String collectionName) {
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    return this;
  }

  public MongoDistributedLocks withLockDuration(Duration duration) {
    this.duration = expectNonNull(duration, "Expected non null duration");
    return this;
  }

  public MongoDistributedLocks withServiceInstanceId(String instanceId) {
    this.instanceId = InstanceId.of(instanceId);
    return this;
  }

  public MongoDistributedLocks withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  public DistributedLocks build() {
    expectNonNull(mongoClient, "Expected non null mongoClient");
    expectNonEmpty(databaseName, "Expected non empty databaseName");
    MongoDistributedLockDriver driver = new MongoDistributedLockDriver(
        mongoClient, databaseName, collectionName, clock);
    return new DistributedLocks(driver, instanceId, duration);
  }
}
