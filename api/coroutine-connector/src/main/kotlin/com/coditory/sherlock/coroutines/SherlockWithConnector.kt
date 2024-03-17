package com.coditory.sherlock.coroutines

import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.DistributedLockBuilder.LockCreator
import com.coditory.sherlock.LockDuration
import com.coditory.sherlock.LockId
import com.coditory.sherlock.OwnerId
import com.coditory.sherlock.OwnerIdPolicy
import com.coditory.sherlock.Preconditions
import com.coditory.sherlock.coroutines.DelegatingDistributedLock.AcquireAction
import com.coditory.sherlock.coroutines.DelegatingDistributedLock.ReleaseAction
import org.slf4j.LoggerFactory

class SherlockWithConnector(
    connector: SuspendingDistributedLockConnector,
    defaultOwnerIdPolicy: OwnerIdPolicy,
    defaultDuration: LockDuration,
) : Sherlock {
    private val logger = LoggerFactory.getLogger(SherlockWithConnector::class.java)
    private val connector: SuspendingDistributedLockConnector
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

    override fun createLock(): DistributedLockBuilder<DistributedLock> {
        return createLockBuilder({ connector.acquire(it) }) { lockId: LockId, ownerId: OwnerId ->
            connector.release(lockId, ownerId)
        }
    }

    override fun createReentrantLock(): DistributedLockBuilder<DistributedLock> {
        return createLockBuilder({ connector.acquireOrProlong(it) }) { lockId: LockId, ownerId: OwnerId ->
            connector.release(lockId, ownerId)
        }
    }

    override fun createOverridingLock(): DistributedLockBuilder<DistributedLock> {
        return createLockBuilder({ connector.forceAcquire(it) }) { id: LockId, _: OwnerId ->
            connector.forceRelease(id)
        }
    }

    override suspend fun forceReleaseAllLocks(): Boolean {
        return connector.forceReleaseAll()
    }

    private fun createLockBuilder(
        acquireAction: AcquireAction,
        releaseAction: ReleaseAction,
    ): DistributedLockBuilder<DistributedLock> {
        return DistributedLockBuilder(createLock(acquireAction, releaseAction))
            .withLockDuration(defaultDuration.value)
            .withOwnerIdPolicy(defaultOwnerIdPolicy)
    }

    private fun createLock(
        acquireAction: AcquireAction,
        releaseAction: ReleaseAction,
    ): LockCreator<DistributedLock> {
        return LockCreator<DistributedLock> { lockId: LockId, duration: LockDuration, ownerId: OwnerId ->
            createLockAndLog(acquireAction, releaseAction, lockId, ownerId, duration)
        }
    }

    private fun createLockAndLog(
        acquireAction: AcquireAction,
        releaseAction: ReleaseAction,
        lockId: LockId,
        ownerId: OwnerId,
        duration: LockDuration,
    ): DistributedLock {
        val lock: DistributedLock =
            DelegatingDistributedLock(acquireAction, releaseAction, lockId, ownerId, duration)
        logger.debug("Created lock: {}", lock)
        return lock
    }
}
