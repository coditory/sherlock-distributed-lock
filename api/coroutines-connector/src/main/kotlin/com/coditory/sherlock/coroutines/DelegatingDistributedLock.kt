package com.coditory.sherlock.coroutines

import com.coditory.sherlock.LockRequest
import com.coditory.sherlock.Preconditions.expectNonNull
import org.slf4j.LoggerFactory
import java.time.Duration

class DelegatingDistributedLock(
    private val acquireAction: AcquireAction,
    private val releaseAction: ReleaseAction,
    private val lockId: String,
    private val ownerId: String,
    private val duration: Duration?,
) : DistributedLock {
    private val logger = LoggerFactory.getLogger(javaClass)
    override val id: String = lockId

    override fun toString(): String {
        return "DelegatingDistributedLock{" +
            "lockId=" + lockId +
            ", ownerId=" + ownerId +
            ", duration=" + duration +
            '}'
    }

    override suspend fun acquire(): Boolean {
        return acquire(LockRequest(lockId, ownerId, duration))
    }

    override suspend fun acquire(duration: Duration): Boolean {
        expectNonNull(duration, "duration")
        return acquire(LockRequest(lockId, ownerId, duration))
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
        suspend fun release(lockId: String, ownerId: String): Boolean
    }

    fun interface AcquireAction {
        suspend fun acquire(lockRequest: LockRequest): Boolean
    }
}
