package com.coditory.sherlock

import kotlinx.coroutines.runBlocking

class BlockingKtSherlockWrapper(
    private val locks: KtSherlock
) : Sherlock {
    override fun initialize() = runBlocking {
        locks.initialize()
    }

    override fun createLock(): DistributedLockBuilder<DistributedLock> {
        return blockingLockBuilder(locks.createLock())
    }

    override fun createReentrantLock(): DistributedLockBuilder<DistributedLock> {
        return blockingLockBuilder(locks.createReentrantLock())
    }

    override fun createOverridingLock(): DistributedLockBuilder<DistributedLock> {
        return blockingLockBuilder(locks.createOverridingLock())
    }

    override fun forceReleaseAllLocks(): Boolean = runBlocking {
        locks.forceReleaseAllLocks()
    }

    override fun forceReleaseLock(lockId: String): Boolean {
        return createOverridingLock(lockId)
            .release()
    }

    private fun blockingLockBuilder(
        builder: DistributedLockBuilder<KtDistributedLock>
    ): DistributedLockBuilder<DistributedLock> {
        return builder.withMappedLock { lock -> BlockingKtDistributedLock(lock) }
    }

    companion object {
        @JvmStatic
        fun blockingKtSherlock(locks: KtSherlock): Sherlock {
            return BlockingKtSherlockWrapper(locks)
        }
    }
}