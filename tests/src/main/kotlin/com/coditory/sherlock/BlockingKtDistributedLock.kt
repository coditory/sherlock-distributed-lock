package com.coditory.sherlock

import kotlinx.coroutines.runBlocking
import java.time.Duration

class BlockingKtDistributedLock(
    private val lock: com.coditory.sherlock.coroutines.DistributedLock,
) : DistributedLock {
    override fun getId(): String {
        return lock.id
    }

    override fun acquire(): Boolean = runBlocking {
        lock.acquire()
    }

    override fun acquire(duration: Duration): Boolean = runBlocking {
        lock.acquire(duration)
    }

    override fun acquireForever(): Boolean = runBlocking {
        lock.acquireForever()
    }

    override fun release(): Boolean = runBlocking {
        lock.release()
    }
}
