package com.coditory.sherlock.coroutines

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

    suspend fun <T> runLocked(action: suspend () -> T): AcquireResultWithValue<T> {
        return if (acquire()) {
            try {
                val result = action()
                AcquireResultWithValue.acquired(result)
            } finally {
                release()
            }
        } else {
            AcquireResultWithValue.notAcquired()
        }
    }

    suspend fun <T : Any> runLocked(duration: Duration, action: suspend () -> T): AcquireResultWithValue<T> {
        return if (acquire(duration)) {
            try {
                val result = action()
                AcquireResultWithValue.acquired(result)
            } finally {
                release()
            }
        } else {
            AcquireResultWithValue.notAcquired()
        }
    }
}
