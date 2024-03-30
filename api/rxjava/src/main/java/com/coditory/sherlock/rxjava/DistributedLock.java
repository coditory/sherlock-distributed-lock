package com.coditory.sherlock.rxjava;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.AcquireResultWithValue;
import com.coditory.sherlock.connector.ReleaseResult;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.Callable;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * Distributed lock with RxJava API.
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
     * Tries to acquire the lock.
     * <p/>
     * Lock is acquired for a pre-configured duration.
     * I lock is not released manually, it becomes released after expiration time.
     *
     * @return {@link AcquireResult}
     */
    @NotNull
    Single<AcquireResult> acquire();

    /**
     * Tries to acquire the lock for a given duration.
     * <p/>
     * If lock is not released manually, it becomes released after expiration time.
     *
     * @param duration lock expiration time when release is not executed
     * @return {@link AcquireResult}
     */
    @NotNull
    Single<AcquireResult> acquire(@NotNull Duration duration);

    /**
     * Tries to acquire the lock without expiration date.
     * <p>
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * without releasing the lock.
     *
     * @return {@link AcquireResult}
     */
    @NotNull
    Single<AcquireResult> acquireForever();

    /**
     * Tries to release the lock.
     *
     * @return {@link ReleaseResult}
     */
    @NotNull
    Single<ReleaseResult> release();

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param <T>    type emitted when lock is acquired
     * @param single subscribed on lock acquisition
     * @return {@link AcquireResultWithValue#acquiredResult(T)} when lock is acquired,
     * {@link AcquireResultWithValue#rejectedResult()} otherwise.
     * @see DistributedLock#acquire()
     */
    @NotNull
    default <T> Single<AcquireResultWithValue<T>> runLocked(@NotNull Single<T> single) {
        expectNonNull(single, "single");
        return acquire()
            .flatMap(acquireResult -> {
                if (!acquireResult.acquired()) {
                    return Single.just(AcquireResultWithValue.rejectedResult());
                }
                return single.map(AcquireResultWithValue::acquiredResult)
                    .flatMap(result -> release().flatMap(__ -> Single.just(result)))
                    .onErrorResumeNext((Throwable throwable) -> release().flatMap(r -> Single.error(throwable)));
            });
    }

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param callable executed when lock is acquired
     * @param <T>      type emitted when lock is acquired
     * @return {@link AcquireResultWithValue#acquiredResult(T)} when lock is acquired,
     * {@link AcquireResultWithValue#rejectedResult()} otherwise.
     * @see DistributedLock#acquire()
     */
    @NotNull
    default <T> Single<AcquireResultWithValue<T>> runLocked(@NotNull Callable<T> callable) {
        expectNonNull(callable, "callable");
        return runLocked(Single.fromCallable(callable));
    }

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param completable executed when lock is acquired
     * @return {@link AcquireResult#acquiredResult()} when lock is acquired,
     * {@link AcquireResult#rejectedResult()} otherwise.
     * @see DistributedLock#acquire()
     */
    @NotNull
    default Single<AcquireResult> runLocked(@NotNull Completable completable) {
        expectNonNull(completable, "completable");
        return runLocked(completable.andThen(Single.just(true)))
            .map(AcquireResultWithValue::toAcquireResult);
    }

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param runnable executed when lock is acquired
     * @return {@link AcquireResult#acquiredResult()} when lock is acquired,
     * {@link AcquireResult#rejectedResult()} otherwise.
     * @see DistributedLock#acquire()
     */
    @NotNull
    default Single<AcquireResult> runLocked(@NotNull Runnable runnable) {
        expectNonNull(runnable, "runnable");
        return runLocked(Completable.fromRunnable(runnable));
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param <T>      type emitted when lock is acquired
     * @param duration lock expiration time when release is not executed
     * @param single   subscribed on lock acquisition
     * @return {@link AcquireResultWithValue#acquiredResult(T)} when lock is acquired,
     * {@link AcquireResultWithValue#rejectedResult()} otherwise.
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default <T> Single<AcquireResultWithValue<T>> runLocked(@NotNull Duration duration, @NotNull Single<T> single) {
        expectNonNull(duration, "duration");
        expectNonNull(single, "single");
        return acquire(duration)
            .flatMap(acquireResult -> {
                if (!acquireResult.acquired()) {
                    return Single.just(AcquireResultWithValue.rejectedResult());
                }
                return single.map(AcquireResultWithValue::acquiredResult)
                    .flatMap(result -> release().flatMap(__ -> Single.just(result)))
                    .onErrorResumeNext((Throwable throwable) -> release().flatMap(r -> Single.error(throwable)));
            });
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param <T>      type emitted when lock is acquired
     * @param duration lock expiration time when release is not executed
     * @param callable executed when lock is acquired
     * @return {@link AcquireResultWithValue#acquiredResult(T)} when lock is acquired,
     * {@link AcquireResultWithValue#rejectedResult()} otherwise.
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default <T> Single<AcquireResultWithValue<T>> runLocked(@NotNull Duration duration, @NotNull Callable<T> callable) {
        expectNonNull(duration, "duration");
        expectNonNull(callable, "callable");
        return runLocked(duration, Single.fromCallable(callable));
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param duration lock expiration time when release is not executed
     * @param runnable executed when lock is acquired
     * @return {@link AcquireResult#acquiredResult()} when lock is acquired,
     * {@link AcquireResult#rejectedResult()} otherwise.
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default Single<AcquireResult> runLocked(@NotNull Duration duration, @NotNull Runnable runnable) {
        expectNonNull(duration, "duration");
        expectNonNull(runnable, "runnable");
        return runLocked(duration, Completable.fromRunnable(runnable));
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param duration    lock expiration time when release is not executed
     * @param completable executed when lock is acquired
     * @return {@link AcquireResult#acquiredResult()} when lock is acquired,
     * {@link AcquireResult#rejectedResult()} otherwise.
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default Single<AcquireResult> runLocked(@NotNull Duration duration, @NotNull Completable completable) {
        expectNonNull(duration, "duration");
        expectNonNull(completable, "completable");
        return runLocked(duration, completable.andThen(Single.just(true)))
            .map(AcquireResultWithValue::toAcquireResult);
    }
}
