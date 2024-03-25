package com.coditory.sherlock.inmem;

import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.SherlockDefaults;
import com.coditory.sherlock.SherlockWithConnectorBuilder;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;

import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_CLOCK;

/**
 * Builds {@link Sherlock} that that stores locks in memory.
 * <p>
 * Designed for testing purposes only.
 */
public final class InMemorySherlock extends
    SherlockWithConnectorBuilder<InMemorySherlock> {
    private Clock clock = DEFAULT_CLOCK;
    private InMemoryDistributedLockStorage storage = new InMemoryDistributedLockStorage();

    /**
     * @return new instance of the builder
     */
    @NotNull
    public static InMemorySherlock builder() {
        return new InMemorySherlock();
    }

    /**
     * @return new instance iof in memory sherlock with default configuration
     */
    @NotNull
    public static Sherlock create() {
        return InMemorySherlock.builder().build();
    }

    private InMemorySherlock() {
        // deliberately empty
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

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    @Override
    @NotNull
    public Sherlock build() {
        InMemoryDistributedLockConnector connector = new InMemoryDistributedLockConnector(
            clock, storage);
        return super.build(connector);
    }
}
