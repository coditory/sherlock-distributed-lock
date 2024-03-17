package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.Callable;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * A reactive distributed lock with RxJava API.
 *
 * @see Sherlock
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
     * @see DistributedLock#acquire()
     */
    @NotNull
    default <T> Maybe<T> acquireAndExecute(@NotNull Single<T> single) {
        expectNonNull(single, "single");
        return DistributedLockExecutor.executeOnAcquired(acquire(), single, this::release);
    }

    @NotNull
    default <T> Maybe<T> acquireAndExecute(@NotNull Callable<? extends T> callable) {
        expectNonNull(callable, "callable");
        return acquireAndExecute(Single.fromCallable(callable));
    }

    @NotNull
    default Maybe<Boolean> acquireAndExecute(@NotNull Runnable runnable) {
        expectNonNull(runnable, "runnable");
        return acquireAndExecute(Single.fromCallable(() -> {
            runnable.run();
            return true;
        }));
    }

    /**
     * Acquire a lock for a given duration and release it after action is executed.
     *
     * @param <T>      type od value emitted by the action
     * @param duration how much time must pass for the acquired lock to expire
     * @param single   to be executed subscribed to when lock is acquired
     * @return true, if lock is acquired
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default <T> Maybe<T> acquireAndExecute(@NotNull Duration duration, @NotNull Single<T> single) {
        expectNonNull(duration, "duration");
        expectNonNull(single, "single");
        return DistributedLockExecutor
                .executeOnAcquired(acquire(duration), single, this::release);
    }

    @NotNull
    default <T> Maybe<T> acquireAndExecute(@NotNull Duration duration, @NotNull Callable<? extends T> callable) {
        expectNonNull(duration, "duration");
        expectNonNull(callable, "callable");
        return acquireAndExecute(duration, Single.fromCallable(callable));
    }

    @NotNull
    default Maybe<Boolean> acquireAndExecute(@NotNull Duration duration, @NotNull Runnable runnable) {
        expectNonNull(duration, "duration");
        expectNonNull(runnable, "runnable");
        return acquireAndExecute(duration, Single.fromCallable(() -> {
            runnable.run();
            return true;
        }));
    }

    /**
     * Acquire a lock without expiration time and release it after action is executed.
     *
     * @param <T>    type od value emitted by the action
     * @param single to be executed subscribed to when lock is acquired
     * @return true, if lock is acquired
     * @see DistributedLock#acquireForever()
     */
    @NotNull
    default <T> Maybe<T> acquireForeverAndExecute(@NotNull Single<T> single) {
        expectNonNull(single, "single");
        return DistributedLockExecutor
                .executeOnAcquired(acquireForever(), single, this::release);
    }

    @NotNull
    default <T> Maybe<T> acquireForeverAndExecute(@NotNull Callable<? extends T> callable) {
        expectNonNull(callable, "callable");
        return acquireForeverAndExecute(Single.fromCallable(callable));
    }

    @NotNull
    default Maybe<Boolean> acquireForeverAndExecute(@NotNull Runnable runnable) {
        expectNonNull(runnable, "runnable");
        return acquireForeverAndExecute(Single.fromCallable(() -> {
            runnable.run();
            return true;
        }));
    }

    /**
     * Run the action when lock is released
     *
     * @param <T>    type od value emitted by the action
     * @param single to be executed subscribed to when lock is released
     * @return true, if lock was release
     * @see DistributedLock#release()
     */
    @NotNull
    default <T> Maybe<T> releaseAndExecute(@NotNull Single<T> single) {
        expectNonNull(single, "single");
        return DistributedLockExecutor
                .executeOnReleased(release(), single);
    }


    @NotNull
    default <T> Maybe<T> releaseAndExecute(@NotNull Callable<? extends T> callable) {
        expectNonNull(callable, "callable");
        return releaseAndExecute(Single.fromCallable(callable));
    }

    @NotNull
    default Maybe<Boolean> releaseAndExecute(@NotNull Runnable runnable) {
        expectNonNull(runnable, "runnable");
        return releaseAndExecute(Single.fromCallable(() -> {
            runnable.run();
            return true;
        }));
    }
}
