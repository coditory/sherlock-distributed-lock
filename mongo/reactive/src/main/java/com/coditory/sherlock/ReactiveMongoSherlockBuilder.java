package com.coditory.sherlock;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link ReactiveSherlock} that uses MongoDB for locking mechanism.
 */
public final class ReactiveMongoSherlockBuilder extends
        ReactiveSherlockWithConnectorBuilder<ReactiveMongoSherlockBuilder> {
    private MongoCollection<Document> collection;
    private Clock clock = DEFAULT_CLOCK;

    /**
     * @return new instance of the builder
     */
    public static ReactiveMongoSherlockBuilder reactiveMongoSherlock() {
        return new ReactiveMongoSherlockBuilder();
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return new instance of mongo sherlock with default configuration
     */
    public static ReactiveSherlock reactiveMongoSherlock(MongoCollection<Document> collection) {
        return reactiveMongoSherlock()
                .withLocksCollection(collection)
                .build();
    }

    private ReactiveMongoSherlockBuilder() {
        // deliberately empty
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return the instance
     */
    public ReactiveMongoSherlockBuilder withLocksCollection(MongoCollection<Document> collection) {
        this.collection = expectNonNull(collection, "Expected non null collection");
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    public ReactiveMongoSherlockBuilder withClock(Clock clock) {
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
