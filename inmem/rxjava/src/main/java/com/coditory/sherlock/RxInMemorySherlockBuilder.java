package com.coditory.sherlock;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link RxSherlock} that that stores locks in memory.
 * <p>
 * Designed for testing purposes only.
 */
public final class RxInMemorySherlockBuilder extends RxSherlockWithConnectorBuilder<RxInMemorySherlockBuilder> {
    private InMemoryDistributedLockStorage storage = new InMemoryDistributedLockStorage();
    private Clock clock = DEFAULT_CLOCK;

    /**
     * @return new instance of the builder
     */
    public static RxInMemorySherlockBuilder rxInMemorySherlockBuilder() {
        return new RxInMemorySherlockBuilder();
    }

    /**
     * @return new instance of in-memory sherlock with default configuration
     */
    public static RxSherlock rxInMemorySherlock() {
        return rxInMemorySherlockBuilder().build();
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    public RxInMemorySherlockBuilder withClock(Clock clock) {
        this.clock = expectNonNull(clock, "Expected non null clock");
        return this;
    }

    /**
     * Use shared stage for all instances of {@link RxSherlock}.
     *
     * @return the instance
     */
    public RxInMemorySherlockBuilder withSharedStorage() {
        this.storage = InMemoryDistributedLockStorage.singleton();
        return this;
    }

    @Override
    public RxSherlock build() {
        RxInMemoryDistributedLockConnector connector = new RxInMemoryDistributedLockConnector(
            clock, storage);
        return super.build(connector);
    }
}
