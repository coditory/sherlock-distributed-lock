package com.coditory.sherlock.reactor;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * A reactive distributed lock with Reactor API.
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
     * Try to acquire a lock. Lock is acquired for a pre-configured duration.
     *
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    @NotNull
    Mono<AcquireResult> acquire();

    /**
     * Try to acquire the lock for a given duration.
     *
     * @param duration how much time must pass for the acquired lock to expire
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    @NotNull
    Mono<AcquireResult> acquire(@NotNull Duration duration);

    /**
     * Try to acquire the lock without expiration date.
     * <p>
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * without releasing the lock.
     *
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    @NotNull
    Mono<AcquireResult> acquireForever();

    /**
     * Release the lock.
     *
     * @return {@link ReleaseResult#SUCCESS} if lock was released by this method invocation. If lock
     * has expired or was released earlier  then {@link ReleaseResult#FAILURE} is returned.
     */
    @NotNull
    Mono<ReleaseResult> release();

    /**
     * Acquire a lock and release it after action is executed or fails.
     *
     * @param <T>  type od value emitted by the action
     * @param mono to be executed subscribed to when lock is acquired
     * @return Mono<T> if lock is acquired, Mono.empty() otherwise.
     * @see DistributedLock#acquire()
     */
    @NotNull
    default <T> Mono<T> acquireAndExecute(@NotNull Mono<T> mono) {
        expectNonNull(mono, "mono");
        return DistributedLockExecutor.executeOnAcquired(acquire(), mono, this::release);
    }

    @NotNull
    default <T> Mono<T> acquireAndExecute(@NotNull Callable<? extends T> callable) {
        expectNonNull(callable, "callable");
        return acquireAndExecute(Mono.fromCallable(callable));
    }

    @NotNull
    default Mono<Void> acquireAndExecute(@NotNull Runnable runnable) {
        return acquireAndExecute(Mono.fromRunnable(runnable));
    }

    /**
     * Acquire a lock for a given duration and release it after action is executed.
     *
     * @param <T>      type od value emitted by the action
     * @param duration how much time must pass for the acquired lock to expire
     * @param mono     to be executed subscribed to when lock is acquired
     * @return true, if lock is acquired
     * @see DistributedLock#acquire(Duration)
     */
    @NotNull
    default <T> Mono<T> acquireAndExecute(@NotNull Duration duration, @NotNull Mono<T> mono) {
        expectNonNull(duration, "duration");
        expectNonNull(mono, "mono");
        return DistributedLockExecutor
                .executeOnAcquired(acquire(duration), mono, this::release);
    }

    @NotNull
    default <T> Mono<T> acquireAndExecute(@NotNull Duration duration, @NotNull Callable<? extends T> callable) {
        expectNonNull(duration, "duration");
        expectNonNull(callable, "callable");
        return acquireAndExecute(duration, Mono.fromCallable(callable));
    }

    @NotNull
    default Mono<Void> acquireAndExecute(@NotNull Duration duration, @NotNull Runnable runnable) {
        expectNonNull(duration, "duration");
        expectNonNull(runnable, "runnable");
        return acquireAndExecute(duration, Mono.fromRunnable(runnable));
    }

    /**
     * Acquire a lock without expiration time and release it after action is executed.
     *
     * @param <T>  type od value emitted by the action
     * @param mono to be executed subscribed to when lock is acquired
     * @return true, if lock is acquired
     * @see DistributedLock#acquireForever()
     */
    @NotNull
    default <T> Mono<T> acquireForeverAndExecute(@NotNull Mono<T> mono) {
        expectNonNull(mono, "mono");
        return DistributedLockExecutor
                .executeOnAcquired(acquireForever(), mono, this::release);
    }

    @NotNull
    default <T> Mono<T> acquireForeverAndExecute(@NotNull Callable<? extends T> callable) {
        expectNonNull(callable, "callable");
        return acquireForeverAndExecute(Mono.fromCallable(callable));
    }

    @NotNull
    default Mono<Void> acquireForeverAndExecute(Runnable runnable) {
        expectNonNull(runnable, "runnable");
        return acquireForeverAndExecute(Mono.fromRunnable(runnable));
    }

    /**
     * Run the action when lock is released
     *
     * @param <T>  type od value emitted by the action
     * @param mono to be executed subscribed to when lock is released
     * @return true, if lock was release
     * @see DistributedLock#release()
     */
    @NotNull
    default <T> Mono<T> releaseAndExecute(@NotNull Mono<T> mono) {
        expectNonNull(mono, "mono");
        return DistributedLockExecutor.executeOnReleased(release(), mono);
    }

    @NotNull
    default <T> Mono<T> releaseAndExecute(@NotNull Callable<? extends T> callable) {
        expectNonNull(callable, "callable");
        return releaseAndExecute(Mono.fromCallable(callable));
    }

    @NotNull
    default Mono<Void> releaseAndExecute(@NotNull Runnable runnable) {
        expectNonNull(runnable, "runnable");
        return releaseAndExecute(Mono.fromRunnable(runnable));
    }
}
