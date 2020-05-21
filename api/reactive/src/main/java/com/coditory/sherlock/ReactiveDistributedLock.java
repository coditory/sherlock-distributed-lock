package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

/**
 * A distributed lock with reactive api.
 *
 * @see ReactiveSherlock
 */
public interface ReactiveDistributedLock {
    /**
     * Return the lock id.
     *
     * @return the lock id
     */
    String getId();

    /**
     * Try to acquire the lock. Lock is acquired for a pre configured duration.
     *
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    Publisher<AcquireResult> acquire();

    /**
     * Try to acquire the lock for a given duration.
     *
     * @param duration how much time must pass for the acquired lock to expire
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    Publisher<AcquireResult> acquire(Duration duration);

    /**
     * Try to acquire the lock without expiring date.
     * <p>
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * with out releasing the lock.
     *
     * @return {@link AcquireResult#SUCCESS} if lock is acquired
     */
    Publisher<AcquireResult> acquireForever();

    /**
     * Try to release the lock.
     *
     * @return {@link ReleaseResult#SUCCESS} if lock was released by this method invocation. If lock
     * has expired or was released earlier  then {@link ReleaseResult#FAILURE} is returned.
     */
    Publisher<ReleaseResult> release();
}
