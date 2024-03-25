package com.coditory.sherlock.mongo.reactor;

import com.coditory.sherlock.SherlockDefaults;
import com.coditory.sherlock.reactor.Sherlock;
import com.coditory.sherlock.reactor.SherlockWithConnectorBuilder;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link Sherlock} that uses MongoDB for locking mechanism.
 */
public final class MongoSherlock extends
    SherlockWithConnectorBuilder<MongoSherlock> {
    private MongoCollection<Document> collection;
    private Clock clock = DEFAULT_CLOCK;

    /**
     * @return new instance of the builder
     */
    @NotNull
    public static MongoSherlock builder() {
        return new MongoSherlock();
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return new instance of mongo sherlock with default configuration
     */
    @NotNull
    public static Sherlock create(@NotNull MongoCollection<Document> collection) {
        expectNonNull(collection, "collection");
        return builder()
            .withLocksCollection(collection)
            .build();
    }

    private MongoSherlock() {
        // deliberately empty
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return the instance
     */
    @NotNull
    public MongoSherlock withLocksCollection(@NotNull MongoCollection<Document> collection) {
        this.collection = expectNonNull(collection, "collection");
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    @NotNull
    public MongoSherlock withClock(@NotNull Clock clock) {
        this.clock = expectNonNull(clock, "clock");
        return this;
    }

    @Override
    @NotNull
    public Sherlock build() {
        expectNonNull(collection, "collection");
        MongoDistributedLockConnector connector = new MongoDistributedLockConnector(
            collection, clock);
        return build(connector);
    }
}
