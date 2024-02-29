package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * A reactive distributed lock with RxJava API.
 *
 * @see RxSherlock
 */
public interface RxDistributedLock {
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
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    @NotNull
    Single<AcquireResult> acquire();

    /**
     * Try to acquire the lock for a given duration.
     *
     * @param duration how much time must pass for the acquired lock to expire
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    @NotNull
    Single<AcquireResult> acquire(@NotNull Duration duration);

    /**
     * Try to acquire the lock without expiration date.
     * <p>
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * without releasing the lock.
     *
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    @NotNull
    Single<AcquireResult> acquireForever();

    /**
     * Release the lock.
     *
     * @return {@link ReleaseResult#SUCCESS} if lock was released by this method invocation. If lock
     * has expired or was released earlier  then {@link ReleaseResult#FAILURE} is returned.
     */
    @NotNull
    Single<ReleaseResult> release();

    /**
     * Acquire a lock and release it after action is executed or fails.
     *
     * @param <T>    type od value emitted by the action
     * @param single to be executed subscribed to when lock is acquired
     * @return Maybe<T> if lock is acquired, empty otherwise.
     * @see RxDistributedLock#acquire()
     */
    @NotNull
    default <T> Maybe<T> acquireAndExecute(@NotNull Single<T> single) {
        expectNonNull(single, "single");
        return RxDistributedLockExecutor.executeOnAcquired(acquire(), single, this::release);
    }

    /**
     * Acquire a lock for a given duration and release it after action is executed.
     *
     * @param <T>      type od value emitted by the action
     * @param duration how much time must pass for the acquired lock to expire
     * @param single   to be executed subscribed to when lock is acquired
     * @return true, if lock is acquired
     * @see RxDistributedLock#acquire(Duration)
     */
    @NotNull
    default <T> Maybe<T> acquireAndExecute(@NotNull Duration duration, @NotNull Single<T> single) {
        expectNonNull(duration, "duration");
        expectNonNull(single, "single");
        return RxDistributedLockExecutor
                .executeOnAcquired(acquire(duration), single, this::release);
    }

    /**
     * Acquire a lock without expiration time and release it after action is executed.
     *
     * @param <T>    type od value emitted by the action
     * @param single to be executed subscribed to when lock is acquired
     * @return true, if lock is acquired
     * @see RxDistributedLock#acquireForever()
     */
    @NotNull
    default <T> Maybe<T> acquireForeverAndExecute(@NotNull Single<T> single) {
        expectNonNull(single, "single");
        return RxDistributedLockExecutor
                .executeOnAcquired(acquireForever(), single, this::release);
    }

    /**
     * Run the action when lock is released
     *
     * @param <T>    type od value emitted by the action
     * @param single to be executed subscribed to when lock is released
     * @return true, if lock was release
     * @see RxDistributedLock#release()
     */
    @NotNull
    default <T> Maybe<T> releaseAndExecute(@NotNull Single<T> single) {
        expectNonNull(single, "single");
        return RxDistributedLockExecutor
                .executeOnReleased(release(), single);
    }
}
