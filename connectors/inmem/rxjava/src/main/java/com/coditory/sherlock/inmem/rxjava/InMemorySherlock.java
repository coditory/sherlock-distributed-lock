package com.coditory.sherlock.inmem.rxjava;

import com.coditory.sherlock.SherlockDefaults;
import com.coditory.sherlock.inmem.InMemoryDistributedLockStorage;
import com.coditory.sherlock.rxjava.Sherlock;
import com.coditory.sherlock.rxjava.SherlockWithConnectorBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link Sherlock} that that stores locks in memory.
 * <p>
 * Designed for testing purposes only.
 */
public final class InMemorySherlock extends SherlockWithConnectorBuilder<InMemorySherlock> {
    private InMemoryDistributedLockStorage storage = new InMemoryDistributedLockStorage();
    private Clock clock = DEFAULT_CLOCK;

    /**
     * @return new instance of the builder
     */
    @NotNull
    public static InMemorySherlock builder() {
        return new InMemorySherlock();
    }

    /**
     * @return new instance of in-memory sherlock with default configuration
     */
    @NotNull
    public static Sherlock create() {
        return builder().build();
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    @NotNull
    public InMemorySherlock withClock(@NotNull Clock clock) {
        this.clock = expectNonNull(clock, "clock");
        return this;
    }

    /**
     * Use shared stage for all instances of {@link Sherlock}.
     *
     * @return the instance
     */
    @NotNull
    public InMemorySherlock withSharedStorage() {
        this.storage = InMemoryDistributedLockStorage.singleton();
        return this;
    }

    @Override
    @NotNull
    public Sherlock build() {
        InMemoryDistributedLockConnector connector = new InMemoryDistributedLockConnector(
                clock, storage);
        return super.build(connector);
    }
}
