package com.coditory.sherlock.coroutines

import com.coditory.sherlock.base.SpecSimulatedException
import com.coditory.sherlock.connector.AcquireResultWithValue
import com.coditory.sherlock.coroutines.base.TestTuple1
import com.coditory.sherlock.coroutines.base.assertThrows
import com.coditory.sherlock.coroutines.base.runDynamicTest
import com.coditory.sherlock.coroutines.base.testTuple
import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.acquiredInMemoryLock
import com.coditory.sherlock.coroutines.test.DistributedLockMock.Companion.releasedInMemoryLock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory
import java.time.Duration

class DistributedLockSpec {
    private val counter = Counter()

    fun setup() {
        counter.reset()
    }

    @TestFactory
    fun `should release the lock after executing the action`() =
        listOf<TestTuple1<suspend (DistributedLock) -> AcquireResultWithValue<Int>>>(
            testTuple("acquireAndExecute") { it.callLocked { counter.incrementAndGet() } },
            testTuple("acquireAndExecute(duration)") { it.callLocked(Duration.ofHours(1)) { counter.incrementAndGet() } },
        ).runDynamicTest {
            // prepare
            val action = it.first
            setup()
            // given
            val lock = releasedInMemoryLock()
            var doOnAcquiredExecuteCount = 0
            var doOnNotAcquiredExecuteCount = 0
            // when
            val result =
                action(lock)
                    .onAcquired { doOnAcquiredExecuteCount++ }
                    .onRejected { doOnNotAcquiredExecuteCount++ }
                    .acquired()
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
        listOf<TestTuple1<suspend (DistributedLock) -> AcquireResultWithValue<Int>>>(
            testTuple("acquireAndExecute") { it.callLocked { counter.incrementAndGet() } },
            testTuple("acquireAndExecute(duration)") { it.callLocked(Duration.ofHours(1)) { counter.incrementAndGet() } },
        ).runDynamicTest {
            // prepare
            val action = it.first
            setup()
            // given
            val lock = acquiredInMemoryLock()
            var doOnAcquiredExecuteCount = 0
            var doOnNotAcquiredExecuteCount = 0
            // when
            val result =
                action(lock)
                    .onAcquired { doOnAcquiredExecuteCount++ }
                    .onRejected { doOnNotAcquiredExecuteCount++ }
                    .acquired()
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
        listOf<TestTuple1<suspend (DistributedLock) -> AcquireResultWithValue<Int>>>(
            testTuple("acquireAndExecute") { it.callLocked { throw SpecSimulatedException() } },
            testTuple(
                "acquireAndExecute(duration)",
            ) { it.callLocked(Duration.ofHours(1)) { throw SpecSimulatedException() } },
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
