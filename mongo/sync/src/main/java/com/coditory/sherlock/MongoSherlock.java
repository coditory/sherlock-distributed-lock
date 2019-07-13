package com.coditory.sherlock;

import com.coditory.sherlock.common.LockDuration;
import com.coditory.sherlock.common.OwnerId;
import com.mongodb.client.MongoClient;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_DB_TABLE_NAME;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_INSTANCE_ID;
import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.common.util.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

/**
 * Builds {@link Sherlock} that uses MongoDB for locking mechanism.
 */
public class MongoSherlock {
  private MongoClient mongoClient;
  private String databaseName;
  private String collectionName = DEFAULT_DB_TABLE_NAME;
  private LockDuration duration = DEFAULT_LOCK_DURATION;
  private OwnerId ownerId = DEFAULT_INSTANCE_ID;
  private Clock clock = DEFAULT_CLOCK;

  /**
   * @return new instance of the builder
   */
  public static MongoSherlock builder() {
    return new MongoSherlock();
  }

  private MongoSherlock() {
    // deliberately empty
  }

  /**
   * @param mongoClient mongo client to be used for locking
   * @return the instance
   */
  public MongoSherlock withMongoClient(MongoClient mongoClient) {
    this.mongoClient = expectNonNull(mongoClient, "Expected non null mongoClient");
    return this;
  }

  /**
   * @param databaseName database name where locks will be stored
   * @return the instance
   */
  public MongoSherlock withDatabaseName(String databaseName) {
    this.databaseName = expectNonEmpty(databaseName, "Expected non empty databaseName");
    return this;
  }

  /**
   * @param collectionName collection name where locks will be stored. Default: {@link
   *     com.coditory.sherlock.common.SherlockDefaults#DEFAULT_DB_TABLE_NAME}
   * @return the instance
   */
  public MongoSherlock withCollectionName(String collectionName) {
    this.collectionName = expectNonEmpty(collectionName, "Expected non empty collectionName");
    return this;
  }

  /**
   * @param duration how much time a lock should be active. When time passes lock is expired and
   *     becomes released. Default: {@link com.coditory.sherlock.common.SherlockDefaults#DEFAULT_LOCK_DURATION}
   * @return the instance
   */
  public MongoSherlock withLockDuration(Duration duration) {
    this.duration = LockDuration.of(duration);
    return this;
  }

  /**
   * @param ownerId owner id most often should be a unique application instance identifier.
   *     Default: {@link com.coditory.sherlock.common.SherlockDefaults#DEFAULT_INSTANCE_ID}
   * @return the instance
   */
  public MongoSherlock withOwnerId(String ownerId) {
    this.ownerId = OwnerId.of(ownerId);
    return this;
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *     com.coditory.sherlock.common.SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public MongoSherlock withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  /**
   * @return sherlock instance
   * @throws IllegalArgumentException when some required values are missing
   */
  public Sherlock build() {
    expectNonNull(mongoClient, "Expected non null mongoClient");
    expectNonEmpty(databaseName, "Expected non empty databaseName");
    MongoDistributedLockConnector connector = new MongoDistributedLockConnector(
        mongoClient, databaseName, collectionName, clock);
    return new SherlockWithConnector(connector, ownerId, duration);
  }
}
