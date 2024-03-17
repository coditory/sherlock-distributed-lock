package com.coditory.sherlock.inmem.rxjava;

import com.coditory.sherlock.SherlockDefaults;
import com.coditory.sherlock.inmem.InMemoryDistributedLockStorage;
import com.coditory.sherlock.rxjava.RxSherlock;
import com.coditory.sherlock.rxjava.RxSherlockWithConnectorBuilder;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    public static RxInMemorySherlockBuilder rxInMemorySherlockBuilder() {
        return new RxInMemorySherlockBuilder();
    }

    /**
     * @return new instance of in-memory sherlock with default configuration
     */
    @NotNull
    public static RxSherlock rxInMemorySherlock() {
        return rxInMemorySherlockBuilder().build();
    }

    /**
     * @param clock time provider used in locking mechanism. Default: {@link
     *              SherlockDefaults#DEFAULT_CLOCK}
     * @return the instance
     */
    @NotNull
    public RxInMemorySherlockBuilder withClock(@NotNull Clock clock) {
        this.clock = expectNonNull(clock, "clock");
        return this;
    }

    /**
     * Use shared stage for all instances of {@link RxSherlock}.
     *
     * @return the instance
     */
    @NotNull
    public RxInMemorySherlockBuilder withSharedStorage() {
        this.storage = InMemoryDistributedLockStorage.singleton();
        return this;
    }

    @Override
    @NotNull
    public RxSherlock build() {
        RxInMemoryDistributedLockConnector connector = new RxInMemoryDistributedLockConnector(
                clock, storage);
        return super.build(connector);
    }
}
