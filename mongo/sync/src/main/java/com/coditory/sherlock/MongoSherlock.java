package com.coditory.sherlock;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.time.Clock;

import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

/**
 * Builds {@link Sherlock} that uses MongoDB for locking mechanism.
 */
public class MongoSherlock extends SherlockWithConnectorBuilder<MongoSherlock> {
  private MongoCollection<Document> collection;
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
   * @param collection mongo collection to be used for locking
   * @return the instance
   */
  public MongoSherlock withMongoCollection(MongoCollection<Document> collection) {
    this.collection = expectNonNull(collection, "Expected non null collection");
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
    expectNonNull(collection, "Expected non null collection");
    MongoDistributedLockConnector connector = new MongoDistributedLockConnector(collection, clock);
    return super.build(connector);
  }
}
