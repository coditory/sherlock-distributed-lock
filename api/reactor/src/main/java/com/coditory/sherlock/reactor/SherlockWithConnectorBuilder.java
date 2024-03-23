package com.coditory.sherlock.reactor;

import com.coditory.sherlock.LockDuration;
import com.coditory.sherlock.OwnerIdPolicy;
import com.coditory.sherlock.SherlockDefaults;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static com.coditory.sherlock.OwnerIdPolicy.staticOwnerId;
import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_LOCK_DURATION;

public abstract class SherlockWithConnectorBuilder<T extends SherlockWithConnectorBuilder<?>> {
    private LockDuration duration = DEFAULT_LOCK_DURATION;
    private OwnerIdPolicy ownerIdPolicy = OwnerIdPolicy.defaultOwnerIdPolicy();

    /**
     * @param duration how much time a lock should be active. When time passes lock is expired and
     *                 becomes released. Default: {@link SherlockDefaults#DEFAULT_LOCK_DURATION}
     * @return the instance
     */
    @NotNull
    public T withLockDuration(@NotNull Duration duration) {
        expectNonNull(duration, "duration");
        this.duration = LockDuration.of(duration);
        return instance();
    }

    /**
     * @param ownerId owner id used to specify who can release an acquired lock
     * @return the instance
     */
    @NotNull
    public T withOwnerId(@NotNull String ownerId) {
        expectNonEmpty(ownerId, "ownerId");
        this.ownerIdPolicy = staticOwnerId(ownerId);
        return instance();
    }

    @NotNull
    public T withOwnerIdPolicy(@NotNull OwnerIdPolicy ownerIdPolicy) {
        this.ownerIdPolicy = ownerIdPolicy;
        return instance();
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    @NotNull
    public abstract Sherlock build();

    @NotNull
    protected Sherlock build(@NotNull DistributedLockConnector connector) {
        return new SherlockWithConnector(connector, ownerIdPolicy, duration);
    }

    @SuppressWarnings("unchecked")
    private T instance() {
        // builder inheritance
        return (T) this;
    }
}

