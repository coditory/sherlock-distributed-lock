package com.coditory.sherlock.mongo.rxjava;

import com.coditory.sherlock.SherlockDefaults;
import com.coditory.sherlock.rxjava.RxSherlock;
import com.coditory.sherlock.rxjava.RxSherlockWithConnectorBuilder;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    public static RxMongoSherlockBuilder rxMongoSherlock() {
        return new RxMongoSherlockBuilder();
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return new instance of mongo sherlock with default configuration
     */
    @NotNull
    public static RxSherlock rxMongoSherlock(@NotNull MongoCollection<Document> collection) {
        expectNonNull(collection, "collection");
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
    @NotNull
    public RxMongoSherlockBuilder withLocksCollection(@NotNull MongoCollection<Document> collection) {
        this.collection = expectNonNull(collection, "collection");
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    @NotNull
    public RxMongoSherlockBuilder withClock(Clock clock) {
        this.clock = expectNonNull(clock, "clock");
        return this;
    }

    @Override
    @NotNull
    public RxSherlock build() {
        expectNonNull(collection, "collection");
        RxMongoDistributedLockConnector connector = new RxMongoDistributedLockConnector(collection, clock);
        return build(connector);
    }
}
