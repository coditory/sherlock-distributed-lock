package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Function;

import static com.coditory.sherlock.OwnerIdPolicy.staticOwnerIdPolicy;
import static com.coditory.sherlock.OwnerIdPolicy.staticUniqueOwnerIdPolicy;
import static com.coditory.sherlock.OwnerIdPolicy.uniqueOwnerIdPolicy;
import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_LOCK_DURATION;
import static com.coditory.sherlock.SherlockDefaults.DEFAULT_OWNER_ID_POLICY;

/**
 * Builds a distributed lock of type T.
 *
 * @param <T> lock type
 */
public final class DistributedLockBuilder<T> {
    private LockId lockId;
    private final LockCreator<T> lockCreator;
    private LockDuration duration = DEFAULT_LOCK_DURATION;
    private OwnerIdPolicy ownerIdPolicy = DEFAULT_OWNER_ID_POLICY;

    DistributedLockBuilder(LockCreator<T> lockCreator) {
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
        this.lockId = LockId.of(lockId);
        return this;
    }

    DistributedLockBuilder<T> withLockDuration(LockDuration duration) {
        this.duration = duration;
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
    public DistributedLockBuilder<T> withLockDuration(@NotNull Duration duration) {
        expectNonNull(duration, "duration");
        return withLockDuration(LockDuration.of(duration));
    }

    /**
     * Overrides the default lock duration so the lock never expires.
     *
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withPermanentLockDuration() {
        this.duration = LockDuration.permanent();
        return this;
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
        return withOwnerIdPolicy(staticOwnerIdPolicy(ownerId));
    }

    /**
     * Overrides the default owner id policy. Created lock will have random and unique id.
     *
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withUniqueOwnerId() {
        return withOwnerIdPolicy(uniqueOwnerIdPolicy());
    }

    /**
     * Overrides the default owner id policy. Created lock will have random identifier shared by all
     * lock instances created in this JVM. Think of it as a static random value.
     *
     * @return the builder
     */
    @NotNull
    public DistributedLockBuilder<T> withStaticUniqueOwnerId() {
        return withOwnerIdPolicy(staticUniqueOwnerIdPolicy());
    }

    DistributedLockBuilder<T> withOwnerIdPolicy(OwnerIdPolicy ownerIdPolicy) {
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
        return lockCreator.createLock(lockId, duration, ownerIdPolicy.getOwnerId());
    }

    @FunctionalInterface
    interface LockCreator<T> {
        @NotNull T createLock(@NotNull LockId lockId, @NotNull LockDuration duration, @NotNull OwnerId ownerId);
    }
}
