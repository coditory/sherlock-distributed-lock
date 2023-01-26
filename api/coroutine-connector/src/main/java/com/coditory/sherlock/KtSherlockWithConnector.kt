package com.coditory.sherlock

import com.coditory.sherlock.DistributedLockBuilder.LockCreator
import com.coditory.sherlock.KtDelegatingDistributedLock.AcquireAction
import com.coditory.sherlock.KtDelegatingDistributedLock.ReleaseAction
import org.slf4j.LoggerFactory

class KtSherlockWithConnector(
    connector: KtDistributedLockConnector,
    defaultOwnerIdPolicy: OwnerIdPolicy,
    defaultDuration: LockDuration
) : KtSherlock {
    private val logger = LoggerFactory.getLogger(KtSherlockWithConnector::class.java)
    private val connector: KtDistributedLockConnector
    private val defaultDuration: LockDuration
    private val defaultOwnerIdPolicy: OwnerIdPolicy

    init {
        Preconditions.expectNonNull(connector, "connector")
        Preconditions.expectNonNull(defaultOwnerIdPolicy, "defaultOwnerIdPolicy")
        Preconditions.expectNonNull(defaultDuration, "defaultDuration")
        this.connector = connector
        this.defaultOwnerIdPolicy = defaultOwnerIdPolicy
        this.defaultDuration = defaultDuration
    }

    override suspend fun initialize() {
        logger.debug("Initializing sherlock infrastructure")
        connector.initialize()
    }

    override fun createLock(): DistributedLockBuilder<KtDistributedLock> {
        return createLockBuilder({ connector.acquire(it) }) { lockId: LockId, ownerId: OwnerId ->
            connector.release(lockId, ownerId)
        }
    }

    override fun createReentrantLock(): DistributedLockBuilder<KtDistributedLock> {
        return createLockBuilder({ connector.acquireOrProlong(it) }) { lockId: LockId, ownerId: OwnerId ->
            connector.release(lockId, ownerId)
        }
    }

    override fun createOverridingLock(): DistributedLockBuilder<KtDistributedLock> {
        return createLockBuilder({ connector.forceAcquire(it) }) { id: LockId, _: OwnerId ->
            connector.forceRelease(id)
        }
    }

    override suspend fun forceReleaseAllLocks(): Boolean {
        return connector.forceReleaseAll()
    }

    private fun createLockBuilder(
        acquireAction: AcquireAction,
        releaseAction: ReleaseAction
    ): DistributedLockBuilder<KtDistributedLock> {
        return DistributedLockBuilder(createLock(acquireAction, releaseAction))
            .withLockDuration(defaultDuration)
            .withOwnerIdPolicy(defaultOwnerIdPolicy)
    }

    private fun createLock(
        acquireAction: AcquireAction,
        releaseAction: ReleaseAction
    ): LockCreator<KtDistributedLock> {
        return LockCreator<KtDistributedLock> { lockId: LockId, duration: LockDuration, ownerId: OwnerId ->
            createLockAndLog(acquireAction, releaseAction, lockId, ownerId, duration)
        }
    }

    private fun createLockAndLog(
        acquireAction: AcquireAction,
        releaseAction: ReleaseAction,
        lockId: LockId,
        ownerId: OwnerId,
        duration: LockDuration
    ): KtDistributedLock {
        val lock: KtDistributedLock =
            KtDelegatingDistributedLock(acquireAction, releaseAction, lockId, ownerId, duration)
        logger.debug("Created lock: {}", lock)
        return lock
    }
}
