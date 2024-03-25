package com.coditory.sherlock.coroutines

import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.lockStub
import com.coditory.sherlock.coroutines.test.SherlockStub
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

class SherlockStubSpec {
    private val lockId = "some-lock"

    @Test
    fun `should create sherlock returning always opened locks`() =
        runTest {
            // given
            val sherlock = SherlockStub.withReleasedLocks()
            // expect
            assertAlwaysOpenedLock(sherlock.createLock(lockId))
            assertAlwaysOpenedLock(sherlock.createReentrantLock(lockId))
            assertAlwaysOpenedLock(sherlock.createOverridingLock(lockId))
        }

    @Test
    fun `should create sherlock returning always closed locks`() =
        runTest {
            // given
            val sherlock = SherlockStub.withAcquiredLocks()
            // expect
            assertAlwaysClosedLock(sherlock.createLock(lockId), lockId)
            assertAlwaysClosedLock(sherlock.createReentrantLock(lockId), lockId)
            assertAlwaysClosedLock(sherlock.createOverridingLock(lockId), lockId)
        }

    @Test
    fun `should create sherlock returning closed locks by default and opened lock for specific id`() =
        runTest {
            // given
            val sherlock =
                SherlockStub.withAcquiredLocks()
                    .withLock(lockStub(lockId, true))
            // expect
            assertAlwaysClosedLock(sherlock.createLock("other-lock"))
            assertAlwaysOpenedLock(sherlock.createLock(lockId))
        }

    private suspend fun assertAlwaysOpenedLock(
        lock: DistributedLock,
        lockId: String = lock.id,
    ) {
        assertSingleStateLock(lock, lockId, true)
    }

    private suspend fun assertAlwaysClosedLock(
        lock: DistributedLock,
        lockId: String = lock.id,
    ) {
        assertSingleStateLock(lock, lockId, false)
    }

    private suspend fun assertSingleStateLock(
        lock: DistributedLock,
        lockId: String,
        expectedResult: Boolean,
    ) {
        assertEquals(lockId, lock.id)
        assertEquals(expectedResult, lock.acquire())
        assertEquals(expectedResult, lock.acquire(Duration.ofHours(1)))
        assertEquals(expectedResult, lock.acquireForever())
        assertEquals(expectedResult, lock.release())
    }
}
