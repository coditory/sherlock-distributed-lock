package com.coditory.sherlock

import java.time.Duration

/**
 * A reactive distributed lock with Reactor API.
 *
 * @see KtSherlock
 */
interface KtDistributedLock {
    /**
     * Return the lock id.
     *
     * @return the lock id
     */
    val id: String

    /**
     * Try to acquire a lock. Lock is acquired for a pre-configured duration.
     *
     * @return true if lock is acquired
     */
    suspend fun acquire(): Boolean

    /**
     * Try to acquire the lock for a given duration.
     *
     * @param duration how much time must pass for the acquired lock to expire
     * @return true if lock is acquired
     */
    suspend fun acquire(duration: Duration): Boolean

    /**
     * Try to acquire the lock without expiration date.
     *
     *
     * It is potentially dangerous. Lookout for a situation when the lock owning instance goes down
     * without releasing the lock.
     *
     * @return true if lock is acquired
     */
    suspend fun acquireForever(): Boolean

    /**
     * Release the lock.
     *
     * @return true if lock was released by this method invocation. If lock
     * has expired or was released earlier then false is returned.
     */
    suspend fun release(): Boolean

    suspend fun <T> acquireAndExecute(action: suspend () -> T): AcquireAndExecuteResult<T> {
        return AcquireAndExecuteResult
            .executeOnAcquired(acquire(), { action() }) { release() }
    }

    suspend fun <T> acquireAndExecute(duration: Duration, action: suspend () -> T): AcquireAndExecuteResult<T> {
        return AcquireAndExecuteResult
            .executeOnAcquired(acquire(duration), { action() }) { release() }
    }

    suspend fun <T> acquireForeverAndExecute(action: suspend () -> T): AcquireAndExecuteResult<T> {
        return AcquireAndExecuteResult
            .executeOnAcquired(acquireForever(), { action() }) { release() }
    }

    suspend fun <T> releaseAndExecute(action: suspend () -> T): ReleaseAndExecuteResult<T> {
        return ReleaseAndExecuteResult
            .executeOnReleased(release()) { action() }
    }

    class AcquireAndExecuteResult<T> internal constructor(
        val acquired: Boolean,
        val result: T?
    ) {

        suspend fun doOnNotAcquired(action: suspend () -> Unit): AcquireAndExecuteResult<T> {
            if (!acquired) {
                action()
            }
            return this
        }

        suspend fun doOnAcquired(action: suspend () -> Unit): AcquireAndExecuteResult<T> {
            if (acquired) {
                action()
            }
            return this
        }

        companion object {
            suspend fun <T> executeOnAcquired(
                acquired: Boolean,
                action: suspend () -> T,
                release: suspend () -> Unit
            ): AcquireAndExecuteResult<T> {
                return if (acquired) {
                    try {
                        val result = action()
                        AcquireAndExecuteResult(true, result)
                    } finally {
                        release()
                    }
                } else {
                    AcquireAndExecuteResult(false, null)
                }
            }
        }
    }

    class ReleaseAndExecuteResult<T> internal constructor(
        val released: Boolean,
        val result: T?
    ) {
        suspend fun doOnReleased(action: suspend () -> Unit): ReleaseAndExecuteResult<T> {
            if (released) {
                action()
            }
            return this
        }

        suspend fun doOnNotReleased(action: suspend () -> Unit): ReleaseAndExecuteResult<T> {
            if (!released) {
                action()
            }
            return this
        }

        companion object {
            suspend fun <T> executeOnReleased(
                released: Boolean,
                action: suspend () -> T
            ): ReleaseAndExecuteResult<T> {
                return if (released) {
                    val result = action()
                    ReleaseAndExecuteResult(true, result)
                } else {
                    ReleaseAndExecuteResult(false, null)
                }
            }
        }
    }
}