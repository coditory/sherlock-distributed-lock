package com.coditory.sherlock.coroutines

import com.coditory.sherlock.DistributedLockBuilder
import com.coditory.sherlock.DistributedLockBuilder.LockCreator
import com.coditory.sherlock.OwnerIdPolicy
import com.coditory.sherlock.Preconditions.expectNonNull
import com.coditory.sherlock.coroutines.DelegatingDistributedLock.AcquireAction
import com.coditory.sherlock.coroutines.DelegatingDistributedLock.ReleaseAction
import org.slf4j.LoggerFactory
import java.time.Duration

class SherlockWithConnector(
    connector: SuspendingDistributedLockConnector,
    defaultOwnerIdPolicy: OwnerIdPolicy,
    defaultDuration: Duration,
) : Sherlock {
    private val logger = LoggerFactory.getLogger(SherlockWithConnector::class.java)
    private val connector: SuspendingDistributedLockConnector
    private val defaultDuration: Duration
    private val defaultOwnerIdPolicy: OwnerIdPolicy

    init {
        expectNonNull(connector, "connector")
        expectNonNull(defaultOwnerIdPolicy, "defaultOwnerIdPolicy")
        expectNonNull(defaultDuration, "defaultDuration")
        this.connector = connector
        this.defaultOwnerIdPolicy = defaultOwnerIdPolicy
        this.defaultDuration = defaultDuration
    }

    override suspend fun initialize() {
        logger.debug("Initializing sherlock infrastructure")
        connector.initialize()
    }

    override fun createLock(): DistributedLockBuilder<DistributedLock> {
        return createLockBuilder({ connector.acquire(it) }) { lockId: String, ownerId: String ->
            connector.release(lockId, ownerId)
        }
    }

    override fun createReentrantLock(): DistributedLockBuilder<DistributedLock> {
        return createLockBuilder({ connector.acquireOrProlong(it) }) { lockId: String, ownerId: String ->
            connector.release(lockId, ownerId)
        }
    }

    override fun createOverridingLock(): DistributedLockBuilder<DistributedLock> {
        return createLockBuilder({ connector.forceAcquire(it) }) { id: String, _: String ->
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
            .withLockDuration(defaultDuration)
            .withOwnerIdPolicy(defaultOwnerIdPolicy)
    }

    private fun createLock(
        acquireAction: AcquireAction,
        releaseAction: ReleaseAction,
    ): LockCreator<DistributedLock> {
        return LockCreator<DistributedLock> { lockId: String, duration: Duration?, ownerId: String ->
            createLockAndLog(acquireAction, releaseAction, lockId, ownerId, duration)
        }
    }

    private fun createLockAndLog(
        acquireAction: AcquireAction,
        releaseAction: ReleaseAction,
        lockId: String,
        ownerId: String,
        duration: Duration?,
    ): DistributedLock {
        val lock: DistributedLock =
            DelegatingDistributedLock(acquireAction, releaseAction, lockId, ownerId, duration)
        logger.debug("Created lock: {}", lock)
        return lock
    }
}
