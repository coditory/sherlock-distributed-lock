package com.coditory.sherlock.reactive;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

import java.time.Clock;

import static com.coditory.sherlock.common.SherlockDefaults.DEFAULT_CLOCK;
import static com.coditory.sherlock.common.util.Preconditions.expectNonNull;

/**
 * Builds {@link ReactiveSherlock} that uses MongoDB for locking mechanism.
 */
public class ReactiveMongoSherlock extends
  ReactiveSherlockWithConnectorBuilder<ReactiveMongoSherlock> {
  private MongoCollection<Document> collection;
  private Clock clock = DEFAULT_CLOCK;

  /**
   * @return new instance of the builder
   */
  public static ReactiveMongoSherlock builder() {
    return new ReactiveMongoSherlock();
  }

  private ReactiveMongoSherlock() {
    // deliberately empty
  }

  /**
   * @param collection mongo collection to be used for locking
   * @return the instance
   */
  public ReactiveMongoSherlock withLocksCollection(MongoCollection<Document> collection) {
    this.collection = expectNonNull(collection, "Expected non null collection");
    return this;
  }

  /**
   * @param clock time provider used in locking mechanism. Default: {@link
   *   com.coditory.sherlock.common.SherlockDefaults#DEFAULT_CLOCK}
   * @return the instance
   */
  public ReactiveMongoSherlock withClock(Clock clock) {
    this.clock = expectNonNull(clock, "Expected non null clock");
    return this;
  }

  @Override
  public ReactiveSherlock build() {
    expectNonNull(collection, "Expected non null collection");
    ReactiveMongoDistributedLockConnector connector = new ReactiveMongoDistributedLockConnector(
      collection, clock);
    return build(connector);
  }
}
