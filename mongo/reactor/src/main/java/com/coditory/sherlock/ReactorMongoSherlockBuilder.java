package com.coditory.sherlock;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link ReactorSherlock} that uses MongoDB for locking mechanism.
 */
public final class ReactorMongoSherlockBuilder extends
        ReactorSherlockWithConnectorBuilder<ReactorMongoSherlockBuilder> {
    private MongoCollection<Document> collection;
    private Clock clock = DEFAULT_CLOCK;

    /**
     * @return new instance of the builder
     */
    @NotNull
    public static ReactorMongoSherlockBuilder reactorMongoSherlock() {
        return new ReactorMongoSherlockBuilder();
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return new instance of mongo sherlock with default configuration
     */
    @NotNull
    public static ReactorSherlock reactorMongoSherlock(@NotNull MongoCollection<Document> collection) {
        expectNonNull(collection, "collection");
        return reactorMongoSherlock()
                .withLocksCollection(collection)
                .build();
    }

    private ReactorMongoSherlockBuilder() {
        // deliberately empty
    }

    /**
     * @param collection mongo collection to be used for locking
     * @return the instance
     */
    @NotNull
    public ReactorMongoSherlockBuilder withLocksCollection(@NotNull MongoCollection<Document> collection) {
        this.collection = expectNonNull(collection, "collection");
        return this;
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    @NotNull
    public ReactorMongoSherlockBuilder withClock(@NotNull Clock clock) {
        this.clock = expectNonNull(clock, "clock");
        return this;
    }

    @Override
    @NotNull
    public ReactorSherlock build() {
        expectNonNull(collection, "collection");
        ReactorMongoDistributedLockConnector connector = new ReactorMongoDistributedLockConnector(
                collection, clock);
        return build(connector);
    }
}
