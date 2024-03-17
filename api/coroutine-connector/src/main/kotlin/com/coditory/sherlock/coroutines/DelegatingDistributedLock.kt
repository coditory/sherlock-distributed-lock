package com.coditory.sherlock.coroutines

import com.coditory.sherlock.LockDuration
import com.coditory.sherlock.LockId
import com.coditory.sherlock.LockRequest
import com.coditory.sherlock.OwnerId
import com.coditory.sherlock.Preconditions
import org.slf4j.LoggerFactory
import java.time.Duration

class DelegatingDistributedLock(
    private val acquireAction: AcquireAction,
    private val releaseAction: ReleaseAction,
    private val lockId: LockId,
    private val ownerId: OwnerId,
    private val duration: LockDuration,
) : DistributedLock {
    private val logger = LoggerFactory.getLogger(javaClass)
    override val id: String = lockId.value

    override suspend fun acquire(): Boolean {
        return acquire(LockRequest(lockId, ownerId, duration))
    }

    override suspend fun acquire(duration: Duration): Boolean {
        Preconditions.expectNonNull(duration, "duration")
        val lockDuration = LockDuration.of(duration)
        return acquire(LockRequest(lockId, ownerId, lockDuration))
    }

    override suspend fun acquireForever(): Boolean {
        return acquire(LockRequest(lockId, ownerId, null))
    }

    override suspend fun release(): Boolean {
        val released = releaseAction.release(lockId, ownerId)
        if (released) {
            logger.debug("Lock released: {}", lockId)
        } else {
            logger.debug("Lock not released: {}", lockId)
        }
        return released
    }

    private suspend fun acquire(lockRequest: LockRequest): Boolean {
        val acquired = acquireAction.acquire(lockRequest)
        if (acquired) {
            logger.debug("Lock acquired: {}, {}", lockId, lockRequest)
        } else {
            logger.debug("Lock not acquired: {}, {}", lockId, lockRequest)
        }
        return acquired
    }

    fun interface ReleaseAction {
        suspend fun release(
            lockId: LockId,
            ownerId: OwnerId,
        ): Boolean
    }

    fun interface AcquireAction {
        suspend fun acquire(lockRequest: LockRequest): Boolean
    }
}
