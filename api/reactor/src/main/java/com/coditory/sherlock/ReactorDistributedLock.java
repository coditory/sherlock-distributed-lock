package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.coditory.sherlock.Preconditions.expectNonNull;

/**
 * A reactive distributed lock with Reactor API.
 *
 * @see ReactorSherlock
 */
public interface ReactorDistributedLock {
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
     * @return true if lock is acquired.
     * @see ReactorDistributedLock#acquire()
     */
    @NotNull
    default <T> Mono<T> acquireAndExecute(@NotNull Mono<T> mono) {
        expectNonNull(mono, "mono");
        return ReactorDistributedLockExecutor.executeOnAcquired(acquire(), mono, this::release);
    }

    /**
     * Acquire a lock for a given duration and release it after action is executed.
     *
     * @param <T>      type od value emitted by the action
     * @param duration how much time must pass for the acquired lock to expire
     * @param mono     to be executed subscribed to when lock is acquired
     * @return true, if lock is acquired
     * @see ReactorDistributedLock#acquire(Duration)
     */
    @NotNull
    default <T> Mono<T> acquireAndExecute(@NotNull Duration duration, @NotNull Mono<T> mono) {
        expectNonNull(duration, "duration");
        expectNonNull(mono, "mono");
        return ReactorDistributedLockExecutor
                .executeOnAcquired(acquire(duration), mono, this::release);
    }

    /**
     * Acquire a lock without expiration time and release it after action is executed.
     *
     * @param <T>  type od value emitted by the action
     * @param mono to be executed subscribed to when lock is acquired
     * @return true, if lock is acquired
     * @see ReactorDistributedLock#acquireForever()
     */
    @NotNull
    default <T> Mono<T> acquireForeverAndExecute(@NotNull Mono<T> mono) {
        expectNonNull(mono, "mono");
        return ReactorDistributedLockExecutor
                .executeOnAcquired(acquireForever(), mono, this::release);
    }

    /**
     * Run the action when lock is released
     *
     * @param <T>  type od value emitted by the action
     * @param mono to be executed subscribed to when lock is released
     * @return true, if lock was release
     * @see ReactorDistributedLock#release()
     */
    @NotNull
    default <T> Mono<T> releaseAndExecute(@NotNull Mono<T> mono) {
        expectNonNull(mono, "mono");
        return ReactorDistributedLockExecutor.executeOnReleased(release(), mono);
    }
}
