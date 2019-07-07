package com.coditory.distributed.lock.reactive;

import com.coditory.distributed.lock.common.InstanceId;
import com.mongodb.reactivestreams.client.MongoClient;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.distributed.lock.common.DistributedLockDefaults.DEFAULT_INSTANCE_ID;
import static com.coditory.distributed.lock.common.DistributedLockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;
import static java.time.Clock.systemDefaultZone;

public class ReactiveMongoDistributedLocks {
  private MongoClient mongoClient;
  private String databaseName;
  private String collectionName = "locks";
  private Duration duration = DEFAULT_LOCK_DURATION;
  private InstanceId instanceId = DEFAULT_INSTANCE_ID;
  private Clock clock = systemDefaultZone();

  public static ReactiveMongoDistributedLocks builder() {
    return new ReactiveMongoDistributedLocks();
  }

  private ReactiveMongoDistributedLocks() {
    // deliberately empty
  }

  public ReactiveMongoDistributedLocks withMongoClient(MongoClient mongoClient) {
    this.mongoClient = expectNonNull(mongoClient, "Expected non null mongoClient");
    return this;
  }

  public ReactiveMongoDistributedLocks withDatabaseName(String databaseName) {
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    return this;
  }

  public ReactiveMongoDistributedLocks withCollectionName(String collectionName) {
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    return this;
  }

  public ReactiveMongoDistributedLocks withLockDuration(Duration duration) {
    this.duration = expectNonNull(duration, "Expected non null duration");
    return this;
  }

  public ReactiveMongoDistributedLocks withServiceInstanceId(String instanceId) {
    this.instanceId = InstanceId.of(instanceId);
    return this;
  }

  public ReactiveMongoDistributedLocks withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  public ReactiveDistributedLocks build() {
    expectNonNull(mongoClient, "Expected non null mongoClient");
    expectNonEmpty(databaseName, "Expected non empty databaseName");
    ReactiveMongoDistributedLockDriver driver = new ReactiveMongoDistributedLockDriver(
        mongoClient, databaseName, collectionName, clock);
    return new ReactiveDistributedLocks(driver, instanceId, duration);
  }
}
