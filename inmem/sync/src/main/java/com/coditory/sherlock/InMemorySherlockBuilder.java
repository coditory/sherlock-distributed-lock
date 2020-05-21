package com.coditory.sherlock;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link Sherlock} that that stores locks in memory.
 * <p>
 * Designed for testing purposes only.
 */
public final class InMemorySherlockBuilder extends
        SherlockWithConnectorBuilder<InMemorySherlockBuilder> {
    private Clock clock = DEFAULT_CLOCK;
    private InMemoryDistributedLockStorage storage = new InMemoryDistributedLockStorage();

    /**
     * @return new instance of the builder
     */
    public static InMemorySherlockBuilder inMemorySherlockBuilder() {
        return new InMemorySherlockBuilder();
    }

    /**
     * @return new instance iof in memory sherlock with default configuration
     */
    public static Sherlock inMemorySherlock() {
        return InMemorySherlockBuilder.inMemorySherlockBuilder().build();
    }

    private InMemorySherlockBuilder() {
        // deliberately empty
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    public InMemorySherlockBuilder withClock(Clock clock) {
        this.clock = expectNonNull(clock, "Expected non null clock");
        return this;
    }

    /**
     * Use shared stage for all instances of {@link Sherlock}.
     *
     * @return the instance
     */
    public InMemorySherlockBuilder withSharedStorage() {
        this.storage = InMemoryDistributedLockStorage.singleton();
        return this;
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    @Override
    public Sherlock build() {
        InMemoryDistributedLockConnector connector = new InMemoryDistributedLockConnector(
                clock, storage);
        return super.build(connector);
    }
}
