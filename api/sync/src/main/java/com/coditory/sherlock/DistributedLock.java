package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.AcquireResultWithValue;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Supplier;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * A distributed lock.
 *
 * @see Sherlock
 */
public interface DistributedLock {
    /**
     * Returns the lock id.
     *
     * @return the lock id
     */
    @NotNull
    String getId();

    /**
     * Tries to acquire the lock.
     * <p/>
     * Lock is acquired for a pre-configured duration.
     * I lock is not released manually, it becomes released after expiration time.
     *
     * @return true if lock is acquired
     */
    boolean acquire();

    /**
     * Tries to acquire the lock for a given duration.
     * <p/>
     * If lock is not released manually, it becomes released after expiration time.
     *
     * @param duration lock expiration time when release is not executed
     * @return true if lock is acquired
     */
    boolean acquire(@NotNull Duration duration);

    /**
     * Tries to acquire the lock without expiration date.
     * <p>
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * without releasing the lock.
     *
     * @return true if lock is acquired
     */
    boolean acquireForever();

    /**
     * Tries to release the lock.
     *
     * @return true if lock was released by this method invocation. If lock has expired or was
     * released earlier  then false is returned.
     */
    boolean release();

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param supplier executed when lock is acquired
     * @param <T>      type emitted when lock is acquired
     * @return {@link AcquireResultWithValue#acquired(T)} if lock was acquired
     * @see DistributedLock#acquire()
     */
    default <T> AcquireResultWithValue<T> runLocked(@NotNull Supplier<? extends T> supplier) {
        expectNonNull(supplier, "supplier");
        if (acquire()) {
            try {
                T value = supplier.get();
                return AcquireResultWithValue.acquired(value);
            } finally {
                release();
            }
        }
        return AcquireResultWithValue.notAcquired();
    }

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param runnable executed when lock is acquired
     * @return {@link AcquireResult#acquired()} if lock was acquired
     * @see DistributedLock#acquire()
     */
    default AcquireResult runLocked(@NotNull Runnable runnable) {
        expectNonNull(runnable, "runnable");
        if (acquire()) {
            try {
                runnable.run();
                return AcquireResult.acquired();
            } finally {
                release();
            }
        }
        return AcquireResult.notAcquired();
    }

    /**
     * Acquire a lock for specific duration and release it after action is executed.
     * <p>
     * This is a helper method that makes sure the lock is released when action finishes successfully
     * or throws an exception.
     *
     * @param duration how much time must pass to release the lock
     * @param runnable to be executed when lock is acquired
     * @return {@link AcquireResult#acquired()} if lock was acquired
     * @see DistributedLock#acquire(Duration)
     */
    default AcquireResult runLocked(@NotNull Duration duration, @NotNull Runnable runnable) {
        expectNonNull(duration, "duration");
        expectNonNull(runnable, "runnable");
        if (acquire(duration)) {
            try {
                runnable.run();
                return AcquireResult.acquired();
            } finally {
                release();
            }
        }
        return AcquireResult.notAcquired();
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param <T>      type emitted when lock is acquired
     * @param duration lock expiration time when release is not executed
     * @param supplier executed when lock is acquired
     * @return {@link AcquireResultWithValue#acquired(T)} if lock was acquired
     * @see DistributedLock#acquire(Duration)
     */
    default <T> AcquireResultWithValue<T> runLocked(@NotNull Duration duration, @NotNull Supplier<? extends T> supplier) {
        expectNonNull(duration, "duration");
        expectNonNull(supplier, "supplier");
        if (acquire(duration)) {
            try {
                T value = supplier.get();
                return AcquireResultWithValue.acquired(value);
            } finally {
                release();
            }
        }
        return AcquireResultWithValue.notAcquired();
    }
}
