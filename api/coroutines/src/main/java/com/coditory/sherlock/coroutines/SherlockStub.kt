package com.coditory.sherlock.coroutines

import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.LockDuration
import com.coditory.sherlock.LockId
import com.coditory.sherlock.OwnerId

/**
 * Use to stub [Sherlock] in tests.
 */
class SherlockStub : Sherlock {
    private val locksById: MutableMap<String, DistributedLock> = HashMap()
    private var defaultLockResult = true

    /**
     * Make the stub produce return a predefined lock.
     *
     * @param lock returned when creating a lock with the same id
     * @return the instance
     */
    fun withLock(lock: DistributedLock): SherlockStub {
        locksById[lock.id] = lock
        return this
    }

    private fun withDefaultAcquireResult(result: Boolean): SherlockStub {
        defaultLockResult = result
        return this
    }

    override suspend fun initialize() {
        // deliberately empty
    }

    override fun createLock(): DistributedLockBuilder<DistributedLock> {
        return getLockOrDefault()
    }

    override fun createReentrantLock(): DistributedLockBuilder<DistributedLock> {
        return getLockOrDefault()
    }

    override fun createOverridingLock(): DistributedLockBuilder<DistributedLock> {
        return getLockOrDefault()
    }

    override suspend fun forceReleaseAllLocks(): Boolean {
        return false
    }

    private fun getLockOrDefault(): DistributedLockBuilder<DistributedLock> {
        return DistributedLockBuilder { id: LockId, _: LockDuration, _: OwnerId ->
            val defaultLock = DistributedLockMock.lockStub(id.value, defaultLockResult)
            locksById.getOrDefault(id.value, defaultLock)
        }
    }

    companion object {
        /**
         * Make the stub produce released locks by default
         *
         * @return the instance
         */
        fun withReleasedLocks(): SherlockStub {
            return SherlockStub()
                .withDefaultAcquireResult(true)
        }

        /**
         * Make the stub produce acquired locks by default
         *
         * @return the instance
         */
        fun withAcquiredLocks(): SherlockStub {
            return SherlockStub()
                .withDefaultAcquireResult(false)
        }
    }
}
