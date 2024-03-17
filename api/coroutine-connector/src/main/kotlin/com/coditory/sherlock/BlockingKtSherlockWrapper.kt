package com.coditory.sherlock

import kotlinx.coroutines.runBlocking

class BlockingKtSherlockWrapper(
    private val sherlock: KtSherlock,
) : Sherlock {
    fun unwrap(): KtSherlock = sherlock

    override fun initialize() =
        runBlocking {
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

    override fun forceReleaseAllLocks(): Boolean =
        runBlocking {
            sherlock.forceReleaseAllLocks()
        }

    override fun forceReleaseLock(lockId: String): Boolean {
        return createOverridingLock(lockId)
            .release()
    }

    private fun blockingLockBuilder(builder: DistributedLockBuilder<KtDistributedLock>): DistributedLockBuilder<DistributedLock> {
        return builder.withMappedLock { lock -> BlockingKtDistributedLock(lock) }
    }

    companion object {
        @JvmStatic
        fun blockingKtSherlock(locks: KtSherlock): Sherlock {
            return BlockingKtSherlockWrapper(locks)
        }
    }
}
