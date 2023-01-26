package com.coditory.sherlock

import com.coditory.sherlock.KtDistributedLock.AcquireAndExecuteResult
import com.coditory.sherlock.KtDistributedLockMock.Companion.acquiredInMemoryLock
import com.coditory.sherlock.KtDistributedLockMock.Companion.releasedInMemoryLock
import com.coditory.sherlock.base.SpecSimulatedException
import com.coditory.sherlock.base.TestTuple1
import com.coditory.sherlock.base.assertThrows
import com.coditory.sherlock.base.runDynamicTest
import com.coditory.sherlock.base.testTuple
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class KtDistributedLockSpec {
    private val counter = Counter()

    fun setup() {
        counter.reset()
    }

    @TestFactory
    fun `should release the lock after executing the action`() =
        listOf<TestTuple1<suspend (KtDistributedLock) -> AcquireAndExecuteResult<Int>>>(
            testTuple("acquireAndExecute") { it.acquireAndExecute { counter.incrementAndGet() } },
            testTuple("acquireAndExecute(duration)") { it.acquireAndExecute(Duration.ofHours(1)) { counter.incrementAndGet() } },
            testTuple("acquireForeverAndExecute") { it.acquireForeverAndExecute { counter.incrementAndGet() } }
        ).runDynamicTest {
            // prepare
            val action = it.first
            setup()
            // given
            val lock = releasedInMemoryLock()
            var doOnAcquiredExecuteCount = 0
            var doOnNotAcquiredExecuteCount = 0
            // when
            val result = action(lock)
                .doOnAcquired { doOnAcquiredExecuteCount++ }
                .doOnNotAcquired { doOnNotAcquiredExecuteCount++ }
                .acquired
            // then
            assertEquals(1, lock.acquisitions())
            assertEquals(1, lock.releases())
            assertEquals(1, counter.getValue())
            assertEquals(1, doOnAcquiredExecuteCount)
            assertEquals(0, doOnNotAcquiredExecuteCount)
            assertEquals(true, result)
        }

    @TestFactory
    fun `should not execute action if lock was not acquired`() =
        listOf<TestTuple1<suspend (KtDistributedLock) -> AcquireAndExecuteResult<Int>>>(
            testTuple("acquireAndExecute") { it.acquireAndExecute { counter.incrementAndGet() } },
            testTuple("acquireAndExecute(duration)") { it.acquireAndExecute(Duration.ofHours(1)) { counter.incrementAndGet() } },
            testTuple("acquireForeverAndExecute") { it.acquireForeverAndExecute { counter.incrementAndGet() } }
        ).runDynamicTest {
            // prepare
            val action = it.first
            setup()
            // given
            val lock = acquiredInMemoryLock()
            var doOnAcquiredExecuteCount = 0
            var doOnNotAcquiredExecuteCount = 0
            // when
            val result = action(lock)
                .doOnAcquired { doOnAcquiredExecuteCount++ }
                .doOnNotAcquired { doOnNotAcquiredExecuteCount++ }
                .acquired
            // then
            assertEquals(1, lock.acquisitions())
            assertEquals(0, lock.releases())
            assertEquals(0, counter.getValue())
            assertEquals(0, doOnAcquiredExecuteCount)
            assertEquals(1, doOnNotAcquiredExecuteCount)
            assertEquals(false, result)
        }

    @TestFactory
    fun `should release the lock after action error`() =
        listOf<TestTuple1<suspend (KtDistributedLock) -> AcquireAndExecuteResult<Int>>>(
            testTuple("acquireAndExecute") { it.acquireAndExecute { throw SpecSimulatedException() } },
            testTuple("acquireAndExecute(duration)") { it.acquireAndExecute(Duration.ofHours(1)) { throw SpecSimulatedException() } },
            testTuple("acquireForeverAndExecute") { it.acquireForeverAndExecute { throw SpecSimulatedException() } }
        ).runDynamicTest {
            // prepare
            val action = it.first
            setup()
            // given
            val lock = releasedInMemoryLock()
            // when
            assertThrows({ action(lock) }, SpecSimulatedException::class)
            // and
            assertEquals(1, lock.acquisitions())
            assertEquals(1, lock.releases())
        }

    @Test
    fun `should execute action on lock release`() = runTest {
        // given
        val lock = acquiredInMemoryLock("sample-lock")
        // when
        val result = lock.releaseAndExecute { counter.incrementAndGet() }
        // then
        assertEquals(0, lock.acquisitions())
        assertEquals(1, lock.releases())
        assertEquals(1, counter.getValue())
        assertEquals(1, result.result)
    }

    @Test
    fun `should not execute action when lock was not released`() = runTest {
        // given
        val lock = releasedInMemoryLock("sample-lock")
        // when
        val result = lock.releaseAndExecute { counter.incrementAndGet() }
        // then
        assertEquals(0, lock.acquisitions())
        assertEquals(1, lock.releases())
        assertEquals(0, counter.getValue())
        assertEquals(null, result.result)
    }

    class Counter {
        private var value: Int = 0

        fun getValue(): Int {
            return value
        }

        fun reset() {
            value = 0
        }

        fun incrementAndGet(): Int {
            value++
            return value
        }

        fun incrementAndThrow(): Int {
            value++
            throw SpecSimulatedException()
        }
    }
}
