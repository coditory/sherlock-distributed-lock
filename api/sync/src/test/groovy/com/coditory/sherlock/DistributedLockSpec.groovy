package com.coditory.sherlock

import com.coditory.sherlock.base.SpecSimulatedException
import com.coditory.sherlock.test.DistributedLockMock
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.base.SpecSimulatedException.throwSpecSimulatedException
import static com.coditory.sherlock.test.DistributedLockMock.acquiredInMemoryLock
import static com.coditory.sherlock.test.DistributedLockMock.releasedInMemoryLock

class DistributedLockSpec extends Specification {
    @Shared
    Counter counter = new Counter()

    void setup() {
        counter.reset()
    }

    @Unroll
    def "should release the lock after executing the action"() {
        given:
            DistributedLockMock lock = releasedInMemoryLock()
        when:
            boolean result = action(lock)
                .onNotAcquired({ assertNever() })
                .acquiredResult()
        then:
            counter.value == 1
            result == true
        and:
            lock.acquisitions() == 1
            lock.releases() == 1
        where:
            action << [
                { it.runLocked({ counter.increment() }) },
                { it.runLocked(Duration.ofHours(1), { counter.increment() }) },
            ]
    }

    @Unroll
    def "should not execute action if lock was not acquired"() {
        given:
            DistributedLockMock lock = acquiredInMemoryLock()
        when:
            boolean result = action(lock)
                .onNotAcquired({ counter.increment() })
                .acquired()
        then:
            counter.value == 1
            result == false
        and:
            lock.acquisitions() == 1
            lock.releases() == 0
        where:
            action << [
                { it.runLocked({ assertNever() }) },
                { it.runLocked(Duration.ofHours(1), { assertNever() }) },
            ]
    }

    def "should release the lock after action error"() {
        given:
            DistributedLockMock lock = releasedInMemoryLock()

        when:
            Boolean result = action(lock)
                .onNotAcquired({ assertNever() })
                .acquiredResult()
        then:
            thrown(SpecSimulatedException)
            result == null
        and:
            lock.acquisitions() == 1
            lock.releases() == 1
        where:
            action << [
                { it.runLocked({ throwSpecSimulatedException() }) },
                { it.runLocked(Duration.ofHours(1), { throwSpecSimulatedException() }) },
            ]
    }

    private void assertNever() {
        throw new IllegalStateException("Should be not executed")
    }

    class Counter {
        private int value = 0

        void reset() {
            value = 0
        }

        void increment() {
            ++value
        }
    }
}
