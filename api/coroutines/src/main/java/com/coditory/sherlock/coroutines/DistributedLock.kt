package com.coditory.sherlock.coroutines

import com.coditory.sherlock.connector.AcquireResult
import com.coditory.sherlock.connector.AcquireResultWithValue
import java.time.Duration

/**
 * Distributed lock with Kotlin Coroutines API.
 *
 * @see Sherlock
 */
interface DistributedLock {
    /**
     * Returns the lock id.
     *
     * @return the lock id
     */
    val id: String

    /**
     * Tries to acquire the lock.
     * <p/>
     * Lock is acquired for a pre-configured duration.
     * I lock is not released manually, it becomes released after expiration time.
     *
     * @return true if lock is acquired
     */
    suspend fun acquire(): Boolean

    /**
     * Tries to acquire the lock for a given duration.
     * <p/>
     * If lock is not released manually, it becomes released after expiration time.
     *
     * @param duration how much time must pass for the acquired lock to expire
     * @return true if lock is acquired
     */
    suspend fun acquire(duration: Duration): Boolean

    /**
     * Tries to acquire the lock without expiration date.
     * <p>
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * without releasing the lock.
     *
     * @return true if lock is acquired
     */
    suspend fun acquireForever(): Boolean

    /**
     * Tries to release the lock.
     *
     * @return true if lock was released by this method invocation. If lock
     * has expired or was released earlier then false is returned.
     */
    suspend fun release(): Boolean

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param action executed when lock is acquired
     * @return [AcquireResult.acquiredResult] when lock is acquired,
     * [AcquireResult.rejectedResult] otherwise.
     * @see DistributedLock#acquire()
     */
    suspend fun runLocked(action: suspend () -> Unit): AcquireResult {
        return if (acquire()) {
            try {
                action()
                AcquireResult.acquiredResult()
            } finally {
                release()
            }
        } else {
            AcquireResult.rejectedResult()
        }
    }

    /**
     * Tries to acquire the lock and releases it after action is executed.
     *
     * @param action executed when lock is acquired
     * @param <T>      type emitted when lock is acquired
     * @return [AcquireResultWithValue.acquiredResult] when lock is acquired,
     * [AcquireResultWithValue.rejectedResult] otherwise.
     * @see DistributedLock#acquire()
     */
    suspend fun <T> callLocked(action: suspend () -> T): AcquireResultWithValue<T> {
        return if (acquire()) {
            try {
                val result = action()
                AcquireResultWithValue.acquiredResult(result)
            } finally {
                release()
            }
        } else {
            AcquireResultWithValue.rejectedResult()
        }
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param duration lock expiration time when release is not executed
     * @param action executed when lock is acquired
     * @return [AcquireResult.acquiredResult] when lock is acquired,
     * [AcquireResult.rejectedResult] otherwise.
     * @see DistributedLock#acquire(Duration)
     */
    suspend fun <T : Any> runLocked(duration: Duration, action: suspend () -> Unit): AcquireResult {
        return if (acquire(duration)) {
            try {
                action()
                AcquireResult.acquiredResult()
            } finally {
                release()
            }
        } else {
            AcquireResult.rejectedResult()
        }
    }

    /**
     * Tries to acquire the lock for a given duration and releases it after action is executed.
     *
     * @param <T>      type emitted when lock is acquired
     * @param duration lock expiration time when release is not executed
     * @param action executed when lock is acquired
     * @return [AcquireResultWithValue.acquiredResult] when lock is acquired,
     * [AcquireResultWithValue.rejectedResult] otherwise.
     * @see DistributedLock#acquire(Duration)
     */
    suspend fun <T : Any> callLocked(duration: Duration, action: suspend () -> T): AcquireResultWithValue<T> {
        return if (acquire(duration)) {
            try {
                val result = action()
                AcquireResultWithValue.acquiredResult(result)
            } finally {
                release()
            }
        } else {
            AcquireResultWithValue.rejectedResult()
        }
    }
}
