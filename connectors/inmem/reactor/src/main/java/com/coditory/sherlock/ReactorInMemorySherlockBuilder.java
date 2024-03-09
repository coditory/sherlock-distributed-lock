package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link ReactorSherlock} that that stores locks in memory.
 * <p>
 * Designed for testing purposes only.
 */
public final class ReactorInMemorySherlockBuilder extends
        ReactorSherlockWithConnectorBuilder<ReactorInMemorySherlockBuilder> {
    private InMemoryDistributedLockStorage storage = new InMemoryDistributedLockStorage();
    private Clock clock = DEFAULT_CLOCK;

    /**
     * @return new instance of the builder
     */
    @NotNull
    public static ReactorInMemorySherlockBuilder reactorInMemorySherlockBuilder() {
        return new ReactorInMemorySherlockBuilder();
    }

    /**
     * @return new instance of in-memory sherlock with default configuration
     */
    @NotNull
    public static ReactorSherlock reactorInMemorySherlock() {
        return reactorInMemorySherlockBuilder().build();
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    @NotNull
    public ReactorInMemorySherlockBuilder withClock(@NotNull Clock clock) {
        this.clock = expectNonNull(clock, "clock");
        return this;
    }

    /**
     * Use shared stage for all instances of {@link ReactorSherlock}.
     *
     * @return the instance
     */
    @NotNull
    public ReactorInMemorySherlockBuilder withSharedStorage() {
        this.storage = InMemoryDistributedLockStorage.singleton();
        return this;
    }

    @Override
    public @NotNull ReactorSherlock build() {
        ReactorInMemoryDistributedLockConnector connector =
                new ReactorInMemoryDistributedLockConnector(clock, storage);
        return super.build(connector);
    }
}
