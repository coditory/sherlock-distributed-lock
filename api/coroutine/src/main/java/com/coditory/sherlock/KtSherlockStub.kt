package com.coditory.sherlock

/**
 * Use to stub [KtSherlock] in tests.
 */
class KtSherlockStub : KtSherlock {
    private val locksById: MutableMap<String, KtDistributedLock> = HashMap()
    private var defaultLockResult = true

    /**
     * Make the stub produce return a predefined lock.
     *
     * @param lock returned when creating a lock with the same id
     * @return the instance
     */
    fun withLock(lock: KtDistributedLock): KtSherlockStub {
        locksById[lock.id] = lock
        return this
    }

    private fun withDefaultAcquireResult(result: Boolean): KtSherlockStub {
        defaultLockResult = result
        return this
    }

    override suspend fun initialize() {
        // deliberately empty
    }

    override fun createLock(): DistributedLockBuilder<KtDistributedLock> {
        return getLockOrDefault()
    }

    override fun createReentrantLock(): DistributedLockBuilder<KtDistributedLock> {
        return getLockOrDefault()
    }

    override fun createOverridingLock(): DistributedLockBuilder<KtDistributedLock> {
        return getLockOrDefault()
    }

    override suspend fun forceReleaseAllLocks(): Boolean {
        return false
    }

    private fun getLockOrDefault(): DistributedLockBuilder<KtDistributedLock> {
        return DistributedLockBuilder { id: LockId, _: LockDuration, _: OwnerId ->
            val defaultLock = KtDistributedLockMock.lockStub(id.value, defaultLockResult)
            locksById.getOrDefault(id.value, defaultLock)
        }
    }

    companion object {
        /**
         * Make the stub produce released locks by default
         *
         * @return the instance
         */
        fun withReleasedLocks(): KtSherlockStub {
            return KtSherlockStub()
                .withDefaultAcquireResult(true)
        }

        /**
         * Make the stub produce acquired locks by default
         *
         * @return the instance
         */
        fun withAcquiredLocks(): KtSherlockStub {
            return KtSherlockStub()
                .withDefaultAcquireResult(false)
        }
    }
}
