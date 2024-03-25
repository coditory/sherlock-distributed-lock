package com.coditory.sherlock.inmem.coroutines

import com.coditory.sherlock.LockRequest
import com.coditory.sherlock.coroutines.SuspendingDistributedLockConnector
import com.coditory.sherlock.inmem.InMemoryDistributedLockStorage
import java.time.Clock
import java.time.Instant

internal class KtInMemoryDistributedLockConnector(
    private val clock: Clock,
    private val storage: InMemoryDistributedLockStorage,
) : SuspendingDistributedLockConnector {
    override suspend fun initialize() {
        // deliberately empty
    }

    override suspend fun acquire(lockRequest: LockRequest): Boolean {
        return storage.acquire(lockRequest, now())
    }

    override suspend fun acquireOrProlong(lockRequest: LockRequest): Boolean {
        return storage.acquireOrProlong(lockRequest, now())
    }

    override suspend fun forceAcquire(lockRequest: LockRequest): Boolean {
        return storage.forceAcquire(lockRequest, now())
    }

    override suspend fun release(lockId: String, ownerId: String): Boolean {
        return storage.release(lockId, now(), ownerId)
    }

    override suspend fun forceRelease(lockId: String): Boolean {
        return storage.forceRelease(lockId, now())
    }

    override suspend fun forceReleaseAll(): Boolean {
        return storage.forceReleaseAll(now())
    }

    private fun now(): Instant {
        return clock.instant()
    }
}
