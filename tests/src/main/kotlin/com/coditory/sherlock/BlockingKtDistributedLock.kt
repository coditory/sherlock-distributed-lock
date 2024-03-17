package com.coditory.sherlock

import com.coditory.sherlock.coroutines.KtDistributedLock
import kotlinx.coroutines.runBlocking
import java.time.Duration

class BlockingKtDistributedLock(
    private val lock: KtDistributedLock,
) : DistributedLock {
    override fun getId(): String {
        return lock.id
    }

    override fun acquire(): Boolean =
        runBlocking {
            lock.acquire()
        }

    override fun acquire(duration: Duration): Boolean =
        runBlocking {
            lock.acquire(duration)
        }

    override fun acquireForever(): Boolean =
        runBlocking {
            lock.acquireForever()
        }

    override fun release(): Boolean =
        runBlocking {
            lock.release()
        }
}
