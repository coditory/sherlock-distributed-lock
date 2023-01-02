package com.coditory.sherlock;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link RxSherlock} that uses MongoDB for locking mechanism.
 */
public final class RxMongoSherlockBuilder extends RxSherlockWithConnectorBuilder<RxMongoSherlockBuilder> {
    private MongoCollection<Document> collection;
    private Clock clock = DEFAULT_CLOCK;

    /**
     * @return new instance of the builder
     */
    public static RxMongoSherlockBuilder rxMongoSherlock() {
        return new RxMongoSherlockBuilder();
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return new instance of mongo sherlock with default configuration
     */
    public static RxSherlock rxMongoSherlock(MongoCollection<Document> collection) {
        return rxMongoSherlock()
            .withLocksCollection(collection)
            .build();
    }

    private RxMongoSherlockBuilder() {
        // deliberately empty
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return the instance
     */
    public RxMongoSherlockBuilder withLocksCollection(MongoCollection<Document> collection) {
        this.collection = expectNonNull(collection, "Expected non null collection");
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    public RxMongoSherlockBuilder withClock(Clock clock) {
        this.clock = expectNonNull(clock, "Expected non null clock");
        return this;
    }

    @Override
    public RxSherlock build() {
        expectNonNull(collection, "Expected non null collection");
        RxMongoDistributedLockConnector connector = new RxMongoDistributedLockConnector(
            collection, clock);
        return build(connector);
    }
}
