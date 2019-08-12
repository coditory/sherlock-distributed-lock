package com.coditory.sherlock;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.time.Clock;

import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * Builds {@link Sherlock} that uses MongoDB for locking mechanism.
 */
public final class MongoSherlockBuilder extends SherlockWithConnectorBuilder<MongoSherlockBuilder> {
  private MongoCollection<Document> collection;
  private Clock clock = DEFAULT_CLOCK;

  /**
   * @return new instance of the builder
   */
  public static MongoSherlockBuilder mongoSherlock() {
    return new MongoSherlockBuilder();
  }

  /**
   * @param collection mongo collection to be used for locking
   * @return new instance of mongo sherlock with default configuration
   */
  public static Sherlock mongoSherlock(MongoCollection<Document> collection) {
    return mongoSherlock()
      .withLocksCollection(collection)
      .build();
  }

  private MongoSherlockBuilder() {
    // deliberately empty
  }

  /**
   * @param collection mongo collection to be used for locking
   * @return the instance
   */
  public MongoSherlockBuilder withLocksCollection(MongoCollection<Document> collection) {
    this.collection = expectNonNull(collection, "Expected non null collection");
    return this;
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *   SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public MongoSherlockBuilder withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  /**
   * @return sherlock instance
   * @throws IllegalArgumentException when some required values are missing
   */
  public Sherlock build() {
    expectNonNull(collection, "Expected non null collection");
    MongoDistributedLockConnector connector = new MongoDistributedLockConnector(collection, clock);
    return super.build(connector);
  }
}
