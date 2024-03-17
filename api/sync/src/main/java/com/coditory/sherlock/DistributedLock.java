package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * A distributed lock.
 */
public interface DistributedLock {
    /**
     * Return the lock id.
     *
     * @return the lock id
     */
    @NotNull
    String getId();

    /**
     * Try to acquire the lock. Lock is acquired for a pre-configured duration.
     *
     * @return true if lock is acquired
     */
    boolean acquire();

    /**
     * Try to acquire the lock for a given duration.
     *
     * @param duration how much time must pass for the acquired lock to expire
     * @return true if lock is acquired
     */
    boolean acquire(@NotNull Duration duration);

    /**
     * Try to acquire the lock without expiration date.
     * <p>
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * without releasing the lock.
     *
     * @return true if lock is acquired
     */
    boolean acquireForever();

    /**
     * Release the lock.
     *
     * @return true if lock was released by this method invocation. If lock has expired or was
     * released earlier  then false is returned.
     */
    boolean release();

    /**
     * Acquire a lock and release it after action is executed.
     * <p>
     * This is a helper method that makes sure the lock is released when action finishes successfully
     * or throws an exception.
     *
     * @param action to be executed when lock is acquired
     * @return {@link AcquireAndExecuteResult}
     * @see DistributedLock#acquire()
     */
    @NotNull
    default AcquireAndExecuteResult acquireAndExecute(@NotNull Runnable action) {
        expectNonNull(action, "action");
        return AcquireAndExecuteResult
                .executeOnAcquired(acquire(), action, this::release);
    }

    /**
     * Acquire a lock for specific duration and release it after action is executed.
     * <p>
     * This is a helper method that makes sure the lock is released when action finishes successfully
     * or throws an exception.
     *
     * @param duration how much time must pass to release the lock
     * @param action   to be executed when lock is acquired
     * @return {@link AcquireAndExecuteResult}
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default AcquireAndExecuteResult acquireAndExecute(@NotNull Duration duration, @NotNull Runnable action) {
        expectNonNull(duration, "duration");
        expectNonNull(action, "action");
        return AcquireAndExecuteResult
                .executeOnAcquired(acquire(duration), action, this::release);
    }

    /**
     * Acquire a lock for specific duration and release it after action is executed.
     * <p>
     * This is a helper method that makes sure the lock is released when action finishes successfully
     * or throws an exception.
     *
     * @param action to be executed when lock is acquired
     * @return {@link AcquireAndExecuteResult}
     * @see DistributedLock#acquireForever()
     */
    @NotNull
    default AcquireAndExecuteResult acquireForeverAndExecute(@NotNull Runnable action) {
        expectNonNull(action, "action");
        return AcquireAndExecuteResult
                .executeOnAcquired(acquireForever(), action, this::release);
    }

    /**
     * Run the action if lock is released.
     *
     * @param action to be executed when lock is released
     * @return {@link ReleaseAndExecuteResult}
     * @see DistributedLock#release()
     */
    @NotNull
    default ReleaseAndExecuteResult releaseAndExecute(@NotNull Runnable action) {
        expectNonNull(action, "action");
        return ReleaseAndExecuteResult
                .executeOnReleased(release(), action);
    }

    class AcquireAndExecuteResult {
        @NotNull
        private static AcquireAndExecuteResult executeOnAcquired(
                boolean acquired,
                @NotNull Runnable action,
                @NotNull Runnable release
        ) {
            if (acquired) {
                try {
                    action.run();
                } finally {
                    release.run();
                }
            }
            return new AcquireAndExecuteResult(acquired);
        }

        private final boolean acquired;

        AcquireAndExecuteResult(boolean acquired) {
            this.acquired = acquired;
        }

        public boolean isAcquired() {
            return acquired;
        }

        @NotNull
        public AcquireAndExecuteResult onNotAcquired(@NotNull Runnable action) {
            expectNonNull(action, "action");
            if (!acquired) {
                action.run();
            }
            return this;
        }
    }

    class ReleaseAndExecuteResult {
        private static ReleaseAndExecuteResult executeOnReleased(boolean released, Runnable action) {
            if (released) {
                action.run();
            }
            return new ReleaseAndExecuteResult(released);
        }

        private final boolean released;

        ReleaseAndExecuteResult(boolean released) {
            this.released = released;
        }

        boolean isReleased() {
            return released;
        }

        @NotNull
        public ReleaseAndExecuteResult onNotReleased(@NotNull Runnable action) {
            expectNonNull(action, "action");
            if (!released) {
                action.run();
            }
            return this;
        }
    }
}
