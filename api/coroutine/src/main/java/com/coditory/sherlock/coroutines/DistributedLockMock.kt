package com.coditory.sherlock.coroutines

import com.coditory.sherlock.LockId
import com.coditory.sherlock.Preconditions
import com.coditory.sherlock.UuidGenerator
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class DistributedLockMock private constructor(
    private val lock: DistributedLock,
) : DistributedLock {
    private val releases: AtomicInteger = AtomicInteger(0)
    private val acquisitions: AtomicInteger = AtomicInteger(0)
    private val successfulReleases: AtomicInteger = AtomicInteger(0)
    private val successfulAcquisitions: AtomicInteger = AtomicInteger(0)
    override val id: String
        get() = lock.id

    override suspend fun acquire(): Boolean {
        val acquired = lock.acquire()
        incrementAcquireCounters(acquired)
        return acquired
    }

    override suspend fun acquire(duration: Duration): Boolean {
        return acquire()
    }

    override suspend fun acquireForever(): Boolean {
        return acquire()
    }

    private fun incrementAcquireCounters(acquired: Boolean): Boolean {
        acquisitions.incrementAndGet()
        if (acquired) {
            successfulAcquisitions.incrementAndGet()
        }
        return acquired
    }

    override suspend fun release(): Boolean {
        val released = lock.release()
        incrementReleaseCounters(released)
        return released
    }

    private fun incrementReleaseCounters(released: Boolean): Boolean {
        releases.incrementAndGet()
        if (released) {
            successfulReleases.incrementAndGet()
        }
        return released
    }

    /**
     * @return the count of successful releases
     */
    fun successfulReleases(): Int {
        return successfulReleases.get()
    }

    /**
     * @return the count of successful acquisitions
     */
    fun successfulAcquisitions(): Int {
        return successfulAcquisitions.get()
    }

    /**
     * @return the count of all releases (successful and unsuccessful)
     */
    fun releases(): Int {
        return releases.get()
    }

    /**
     * @return the count of all acquisitions (successful and unsuccessful)
     */
    fun acquisitions(): Int {
        return acquisitions.get()
    }

    /**
     * @return the count of rejected releases
     */
    fun rejectedReleases(): Int {
        return releases() - successfulReleases()
    }

    /**
     * @return the count of rejected acquisitions
     */
    fun rejectedAcquisitions(): Int {
        return acquisitions() - successfulAcquisitions()
    }

    /**
     * @return true if lock was successfully acquired at least once
     */
    fun wasAcquired(): Boolean {
        return successfulAcquisitions() > 0
    }

    /**
     * @return true if lock was successfully released at least once
     */
    fun wasReleased(): Boolean {
        return successfulReleases() > 0
    }

    /**
     * @return true if lock was successfully acquired and released
     */
    fun wasAcquiredAndReleased(): Boolean {
        return wasAcquired() && wasReleased()
    }

    /**
     * @return true if lock was acquired without success at least once
     */
    fun wasAcquireRejected(): Boolean {
        return successfulAcquisitions() < acquisitions()
    }

    /**
     * @return true if lock was released without success at least once
     */
    fun wasReleaseRejected(): Boolean {
        return successfulReleases() < releases()
    }

    /**
     * @return true if acquire operation was invoked at least once
     */
    fun wasAcquireInvoked(): Boolean {
        return acquisitions() > 0
    }

    /**
     * @return true if release operation was invoked at least once
     */
    fun wasReleaseInvoked(): Boolean {
        return releases() > 0
    }

    private class InMemoryDistributedLockStub private constructor(
        lockId: LockId,
        private val reentrant: Boolean,
        acquired: Boolean,
    ) : DistributedLock {
        private val acquired = AtomicBoolean(acquired)

        override val id: String = lockId.value

        override suspend fun acquire(): Boolean {
            return acquireSync()
        }

        private fun acquireSync(): Boolean {
            return if (reentrant) {
                acquired.set(true)
                true
            } else {
                acquired.compareAndSet(false, true)
            }
        }

        override suspend fun acquire(duration: Duration): Boolean {
            return acquire()
        }

        override suspend fun acquireForever(): Boolean {
            return acquire()
        }

        override suspend fun release(): Boolean {
            return releaseSync()
        }

        private fun releaseSync(): Boolean {
            return acquired.compareAndSet(true, false)
        }

        companion object {
            fun reentrantInMemoryLock(
                lockId: LockId,
                acquired: Boolean,
            ): InMemoryDistributedLockStub {
                return InMemoryDistributedLockStub(lockId, true, acquired)
            }

            fun inMemoryLock(
                lockId: LockId,
                acquired: Boolean,
            ): InMemoryDistributedLockStub {
                return InMemoryDistributedLockStub(lockId, false, acquired)
            }
        }
    }

    internal class SequencedDistributedLockStub private constructor(
        lockId: LockId,
        acquireResults: List<Boolean>,
        releaseResults: List<Boolean>,
    ) : DistributedLock {
        private val acquireResults = ConcurrentLinkedQueue(acquireResults)
        private val releaseResults = ConcurrentLinkedQueue(releaseResults)
        private val defaultAcquireResult = acquireResults[acquireResults.size - 1]
        private val defaultReleaseResult = releaseResults[releaseResults.size - 1]

        constructor(
            lockId: String,
            acquireResults: List<Boolean>,
            releaseResults: List<Boolean>,
        ) : this(LockId.of(lockId), acquireResults, releaseResults)

        override val id: String = lockId.value

        override suspend fun acquire(): Boolean {
            return acquireResults.poll() ?: defaultAcquireResult
        }

        override suspend fun acquire(duration: Duration): Boolean {
            return acquire()
        }

        override suspend fun acquireForever(): Boolean {
            return acquire()
        }

        override suspend fun release(): Boolean {
            return releaseResults.poll() ?: defaultReleaseResult
        }
    }

    companion object {
        @JvmOverloads
        fun releasedInMemoryLock(lockId: String = UuidGenerator.uuid()): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            return inMemoryLock(lockId, false)
        }

        @JvmOverloads
        fun acquiredInMemoryLock(lockId: String = UuidGenerator.uuid()): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            return inMemoryLock(lockId, true)
        }

        private fun inMemoryLock(
            lockId: String,
            state: Boolean,
        ): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            return of(InMemoryDistributedLockStub.inMemoryLock(LockId.of(lockId), state))
        }

        @JvmOverloads
        fun releasedReentrantInMemoryLock(lockId: String = UuidGenerator.uuid()): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            return reentrantInMemoryLock(lockId, false)
        }

        @JvmOverloads
        fun acquiredReentrantInMemoryLock(lockId: String = UuidGenerator.uuid()): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            return reentrantInMemoryLock(lockId, true)
        }

        private fun reentrantInMemoryLock(
            lockId: String,
            state: Boolean,
        ): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            return of(InMemoryDistributedLockStub.reentrantInMemoryLock(LockId.of(lockId), state))
        }

        fun lockStub(result: Boolean): DistributedLockMock {
            return lockStub(UuidGenerator.uuid(), result)
        }

        fun lockStub(
            acquireResult: Boolean,
            releaseResult: Boolean,
        ): DistributedLockMock {
            return lockStub(UuidGenerator.uuid(), acquireResult, releaseResult)
        }

        @JvmStatic
        fun lockStub(
            lockId: String,
            result: Boolean,
        ): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            return of(lockStub(lockId, result, result))
        }

        fun lockStub(
            lockId: String,
            acquireResult: Boolean,
            releaseResult: Boolean,
        ): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            val lock = SequencedDistributedLockStub(lockId, listOf(acquireResult), listOf(releaseResult))
            return of(lock)
        }

        fun sequencedLock(
            acquireResults: List<Boolean>,
            releaseResults: List<Boolean>,
        ): DistributedLockMock {
            Preconditions.expectNonNull(acquireResults, "acquireResults")
            Preconditions.expectNonNull(releaseResults, "releaseResults")
            return of(sequencedLock(UuidGenerator.uuid(), acquireResults, releaseResults))
        }

        fun sequencedLock(
            lockId: String,
            acquireResults: List<Boolean>,
            releaseResults: List<Boolean>,
        ): DistributedLockMock {
            Preconditions.expectNonEmpty(lockId, "lockId")
            Preconditions.expectNonNull(acquireResults, "acquireResults")
            Preconditions.expectNonNull(releaseResults, "releaseResults")
            return of(SequencedDistributedLockStub(lockId, acquireResults, releaseResults))
        }

        private fun of(lock: DistributedLock): DistributedLockMock {
            Preconditions.expectNonNull(lock, "lock")
            return DistributedLockMock(lock)
        }
    }
}
