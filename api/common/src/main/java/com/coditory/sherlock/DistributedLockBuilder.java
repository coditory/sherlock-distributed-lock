package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.function.Function;

import static com.coditory.sherlock.OwnerIdPolicy.*;
import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectTruncatedToMillis;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_LOCK_DURATION;

/**
 * Builds a distributed lock of type T.
 *
 * @param <T> lock type
 */
public final class DistributedLockBuilder<T> {
    private String lockId;
    private final LockCreator<T> lockCreator;
    private Duration duration = DEFAULT_LOCK_DURATION;
    private OwnerIdPolicy ownerIdPolicy = uniqueOwnerId();

    public DistributedLockBuilder(LockCreator<T> lockCreator) {
        this.lockCreator = lockCreator;
    }

    <R> DistributedLockBuilder<R> withMappedLock(Function<T, R> lockMapper) {
        return new DistributedLockBuilder<>((lockId, duration, ownerId) -> {
            T lock = lockCreator.createLock(lockId, duration, ownerId);
            return lockMapper.apply(lock);
        });
    }

    /**
     * Set up lock identifier. Lock identifier distinguishes locks in a distributed system.
     *
     * @param lockId the lock identifier
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withLockId(@NotNull String lockId) {
        expectNonEmpty(lockId, "lockId");
        this.lockId = lockId;
        return this;
    }

    /**
     * Overrides the default lock duration. Lock duration is the amount time that must pass to lock
     * expire the lock. Expired lock is treated as released.
     *
     * @param duration lock duration.
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withLockDuration(Duration duration) {
        if (duration != null) {
            expectTruncatedToMillis(duration, "duration");
        }
        this.duration = duration;
        return this;
    }

    /**
     * Overrides the default lock duration so the lock never expires.
     *
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withPermanentLockDuration() {
        return withLockDuration(null);
    }

    /**
     * Overrides the default owner id used to distinguish who acquired the lock.
     *
     * @param ownerId owner identifier
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withOwnerId(@NotNull String ownerId) {
        expectNonEmpty(ownerId, "ownerId");
        return withOwnerIdPolicy(staticOwnerId(ownerId));
    }

    /**
     * Overrides the default owner id policy. Created lock will have random and unique id.
     *
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withUniqueOwnerId() {
        return withOwnerIdPolicy(uniqueOwnerId());
    }

    /**
     * Overrides the default owner id policy. Created lock will have random identifier shared by all
     * lock instances created in this JVM. Think of it as a static random value.
     *
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withStaticUniqueOwnerId() {
        return withOwnerIdPolicy(staticUniqueOwnerId());
    }

    @NotNull
    public DistributedLockBuilder<T> withOwnerIdPolicy(OwnerIdPolicy ownerIdPolicy) {
        this.ownerIdPolicy = ownerIdPolicy;
        return this;
    }

    /**
     * Builds the lock.
     *
     * @return the lock
     */
    @NotNull
    public T build() {
        String ownerId = ownerIdPolicy.getOwnerId();
        expectNonEmpty(ownerId, "ownerId");
        return lockCreator.createLock(lockId, duration, ownerId);
    }

    @FunctionalInterface
    public interface LockCreator<T> {
        @NotNull T createLock(@NotNull String lockId, @Nullable Duration duration, @NotNull String ownerId);
    }
}
