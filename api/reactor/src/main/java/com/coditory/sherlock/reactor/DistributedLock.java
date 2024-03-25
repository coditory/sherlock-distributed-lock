package com.coditory.sherlock.reactor;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * Distributed lock with Reactor API.
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
     * @return {@link AcquireResult}
     */
    @NotNull
    Mono<AcquireResult> acquire();

    /**
     * Tries to acquire the lock for a given duration.
     * <p/>
     * If lock is not released manually, it becomes released after expiration time.
     *
     * @param duration lock expiration time when release is not executed
     * @return {@link AcquireResult}
     */
    @NotNull
    Mono<AcquireResult> acquire(@NotNull Duration duration);

    /**
     * Tries to acquire the lock without expiration date.
     * <p>
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * without releasing the lock.
     *
     * @return {@link AcquireResult}
     */
    @NotNull
    Mono<AcquireResult> acquireForever();

    /**
     * Tries to release the lock.
     *
     * @return {@link ReleaseResult}
     */
    @NotNull
    Mono<ReleaseResult> release();

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param <T>  type emitted when lock is acquired
     * @param mono subscribed on lock acquisition
     * @return {@code Mono<T>} when lock is acquired, {@code Mono.empty()} otherwise
     * @see DistributedLock#acquire()
     */
    @NotNull
    default <T> Mono<T> runLocked(@NotNull Mono<T> mono) {
        expectNonNull(mono, "mono");
        return DistributedLockExecutor.executeOnAcquired(acquire(), mono, this::release);
    }

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param callable executed when lock is acquired
     * @param <T>      type emitted when lock is acquired
     * @return {@code Mono<T>} when lock is acquired, {@code Mono.empty()} otherwise
     * @see DistributedLock#acquire()
     */
    @NotNull
    default <T> Mono<T> runLocked(@NotNull Callable<? extends T> callable) {
        expectNonNull(callable, "callable");
        return runLocked(Mono.fromCallable(callable));
    }

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param runnable executed when lock is acquired
     * @return {@code Mono.just(true)} when lock is acquired, {@code Mono.empty()} otherwise
     * @see DistributedLock#acquire()
     */
    @NotNull
    default Mono<Boolean> runLocked(@NotNull Runnable runnable) {
        expectNonNull(runnable, "runnable");
        return runLocked(() -> {
            runnable.run();
            return true;
        });
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param <T>      type emitted when lock is acquired
     * @param duration lock expiration time when release is not executed
     * @param mono     subscribed on lock acquisition
     * @return {@code Mono<T>} when lock is acquired, {@code Mono.empty()} otherwise
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default <T> Mono<T> runLocked(@NotNull Duration duration, @NotNull Mono<T> mono) {
        expectNonNull(duration, "duration");
        expectNonNull(mono, "mono");
        return DistributedLockExecutor
            .executeOnAcquired(acquire(duration), mono, this::release);
    }
 
    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param <T>      type emitted when lock is acquired
     * @param duration lock expiration time when release is not executed
     * @param callable executed when lock is acquired
     * @return {@code Mono<T>} when lock is acquired, {@code Mono.empty()} otherwise
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default <T> Mono<T> runLocked(@NotNull Duration duration, @NotNull Callable<? extends T> callable) {
        expectNonNull(duration, "duration");
        expectNonNull(callable, "callable");
        return runLocked(duration, Mono.fromCallable(callable));
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param duration lock expiration time when release is not executed
     * @param runnable executed when lock is acquired
     * @return {@code Mono.just(true)} when lock is acquired, {@code Mono.empty()} otherwise
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default Mono<Boolean> runLocked(@NotNull Duration duration, @NotNull Runnable runnable) {
        expectNonNull(duration, "duration");
        expectNonNull(runnable, "runnable");
        return runLocked(duration, () -> {
            runnable.run();
            return true;
        });
    }
}
