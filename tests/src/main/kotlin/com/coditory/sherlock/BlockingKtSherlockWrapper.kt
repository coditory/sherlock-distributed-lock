package com.coditory.sherlock

import kotlinx.coroutines.runBlocking

class BlockingKtSherlockWrapper(
    private val sherlock: com.coditory.sherlock.coroutines.Sherlock,
) : Sherlock {
    fun unwrap(): com.coditory.sherlock.coroutines.Sherlock = sherlock

    override fun initialize() = runBlocking {
        sherlock.initialize()
    }

    override fun createLock(): DistributedLockBuilder<DistributedLock> {
        return blockingLockBuilder(sherlock.createLock())
    }

    override fun createReentrantLock(): DistributedLockBuilder<DistributedLock> {
        return blockingLockBuilder(sherlock.createReentrantLock())
    }

    override fun createOverridingLock(): DistributedLockBuilder<DistributedLock> {
        return blockingLockBuilder(sherlock.createOverridingLock())
    }

    override fun forceReleaseAllLocks(): Boolean = runBlocking {
        sherlock.forceReleaseAllLocks()
    }

    override fun forceReleaseLock(lockId: String): Boolean {
        return createOverridingLock(lockId)
            .release()
    }

    private fun blockingLockBuilder(
        builder: DistributedLockBuilder<com.coditory.sherlock.coroutines.DistributedLock>,
    ): DistributedLockBuilder<DistributedLock> {
        return builder.withMappedLock { lock -> BlockingKtDistributedLock(lock) }
    }
}
