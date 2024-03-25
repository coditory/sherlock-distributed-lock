package com.coditory.sherlock.coroutines

import com.coditory.sherlock.coroutines.base.TestTuple1
import com.coditory.sherlock.coroutines.base.TestTuple3
import com.coditory.sherlock.coroutines.base.runDynamicTest
import com.coditory.sherlock.coroutines.base.testTuple
import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.acquiredInMemoryLock
import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.acquiredReentrantInMemoryLock
import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.lockStub
import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.releasedInMemoryLock
import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.releasedReentrantInMemoryLock
import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.sequencedLock
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class DistributedLockMockSpec {
    private val lockId = "sample-lock"

    @TestFactory
    fun `should create single state lock with`() =
        listOf<TestTuple3<Boolean, Boolean, () -> DistributedLock>>(
            testTuple("singleStateLock(lockId, true)", true, true) { lockStub(lockId, true) },
            testTuple("singleStateLock(lockId, true, false)", true, false) { lockStub(lockId, true, false) },
            testTuple("singleStateLock(true)", false, false) { lockStub(false) },
            testTuple("singleStateLock(true, false)", false, true) { lockStub(false, true) },
        ).runDynamicTest {
            // prepare
            val lock = it.last()
            val acquireResult = it.p1
            val releaseResult = it.p2
            // expect
            assertEquals(acquireResult, lock.acquire())
            assertEquals(acquireResult, lock.acquire())
            // and
            assertEquals(releaseResult, lock.release())
            assertEquals(releaseResult, lock.release())
        }

    @Test
    fun `should create a lock that returns a sequence of results`() =
        runTest {
            // given
            val acquireResultSequence = listOf(true, false, true)
            val releaseResultSequence = listOf(false, true, false)
            // and
            val lock = sequencedLock("sample-lock", acquireResultSequence, releaseResultSequence)
            // expect
            assertEquals(
                acquireResultSequence + true,
                listOf(lock.acquire(), lock.acquire(), lock.acquire(), lock.acquire()),
            )
            assertEquals(
                releaseResultSequence + false,
                listOf(lock.release(), lock.release(), lock.release(), lock.release()),
            )
        }

    @TestFactory
    fun `should create a released in-memory lock`() =
        listOf<TestTuple1<() -> DistributedLock>>(
            testTuple("releasedInMemoryLock()") { releasedInMemoryLock() },
            testTuple("releasedReentrantInMemoryLock()") { releasedReentrantInMemoryLock() },
            testTuple("releasedInMemoryLock(lockId)") { releasedInMemoryLock(lockId) },
            testTuple("releasedReentrantInMemoryLock(lockId)") { releasedReentrantInMemoryLock(lockId) },
        ).runDynamicTest {
            val lock = it.first()
            assertFalse(lock.release())
            assertTrue(lock.acquire())
        }

    @TestFactory
    fun `should create an acquired reentrant in-memory lock`() =
        listOf<TestTuple1<() -> DistributedLock>>(
            testTuple("acquiredReentrantInMemoryLock()") { acquiredReentrantInMemoryLock() },
            testTuple("acquiredReentrantInMemoryLock(lockId)") { acquiredReentrantInMemoryLock(lockId) },
        ).runDynamicTest {
            val lock = it.first()
            assertTrue(lock.release())
            assertTrue(lock.acquire())
            assertTrue(lock.acquire())
        }

    @TestFactory
    fun `should create an acquired single entrant in-memory lock`() =
        listOf<TestTuple1<() -> DistributedLock>>(
            testTuple("acquiredInMemoryLock()") { acquiredInMemoryLock() },
            testTuple("acquiredInMemoryLock(lockId)") { acquiredInMemoryLock(lockId) },
        ).runDynamicTest {
            val lock = it.first()
            assertTrue(lock.release())
            assertTrue(lock.acquire())
            assertFalse(lock.acquire())
        }

    @Test
    fun `should record no invocation for a new lock mock instance`() =
        runTest {
            // given
            val lock = releasedInMemoryLock()
            // expect
            assertEquals(0, lock.acquisitions())
            assertEquals(0, lock.releases())
            // and
            assertEquals(0, lock.successfulAcquisitions())
            assertEquals(0, lock.successfulReleases())
            // and
            assertFalse(lock.wasAcquireInvoked())
            assertFalse(lock.wasReleaseInvoked())
            assertFalse(lock.wasAcquireRejected())
            assertFalse(lock.wasReleaseRejected())
            assertFalse(lock.wasAcquiredAndReleased())
        }

    @Test
    fun `should record lock acquire invocations`() =
        runTest {
            // given
            val lock = releasedInMemoryLock()
            // when
            lock.acquire()
            // then
            assertEquals(1, lock.acquisitions())
            // and
            assertEquals(1, lock.successfulAcquisitions())
            assertEquals(0, lock.rejectedAcquisitions())
            // and
            assertTrue(lock.wasAcquireInvoked())
            assertFalse(lock.wasAcquireRejected())
            assertFalse(lock.wasAcquiredAndReleased())

            // when
            lock.acquire()
            // then
            assertEquals(2, lock.acquisitions())
            // and
            assertEquals(1, lock.successfulAcquisitions())
            assertEquals(1, lock.rejectedAcquisitions())
            // and
            assertTrue(lock.wasAcquireInvoked())
            assertTrue(lock.wasAcquireRejected())
        }

    @Test
    fun `should record lock release invocations`() =
        runTest {
            // given
            val lock = acquiredInMemoryLock()
            // when
            lock.release()
            // then
            assertEquals(1, lock.releases())
            // and
            assertEquals(1, lock.successfulReleases())
            assertEquals(0, lock.rejectedReleases())
            // and
            assertTrue(lock.wasReleaseInvoked())
            assertFalse(lock.wasReleaseRejected())
            assertFalse(lock.wasAcquiredAndReleased())

            // when
            lock.release()
            // then
            assertEquals(2, lock.releases())
            // and
            assertEquals(1, lock.successfulReleases())
            assertEquals(1, lock.rejectedReleases())
            // and
            assertTrue(lock.wasReleaseInvoked())
            assertTrue(lock.wasReleaseRejected())
        }

    @Test
    fun `should record acquire and release invocations`() =
        runTest {
            // given
            val lock = releasedInMemoryLock()
            // when
            lock.acquire()
            lock.release()
            // then
            assertTrue(lock.wasAcquiredAndReleased())
        }
}
