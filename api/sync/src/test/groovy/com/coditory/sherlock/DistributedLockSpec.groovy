package com.coditory.sherlock

import com.coditory.sherlock.base.SpecSimulatedException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.DistributedLockMock.acquiredInMemoryLock
import static com.coditory.sherlock.DistributedLockMock.releasedInMemoryLock
import static com.coditory.sherlock.base.SpecSimulatedException.throwSpecSimulatedException

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
                    .isAcquired()
        then:
            counter.value == 1
            result == true
        and:
            lock.acquisitions() == 1
            lock.releases() == 1
        where:
            action << [
                    { it.acquireAndExecute({ counter.increment() }) },
                    { it.acquireAndExecute(Duration.ofHours(1), { counter.increment() }) },
                    { it.acquireForeverAndExecute({ counter.increment() }) },
            ]
    }

    @Unroll
    def "should not execute action if lock was not acquired"() {
        given:
            DistributedLockMock lock = acquiredInMemoryLock()
        when:
            boolean result = action(lock)
                    .onNotAcquired({ counter.increment() })
                    .isAcquired()
        then:
            counter.value == 1
            result == false
        and:
            lock.acquisitions() == 1
            lock.releases() == 0
        where:
            action << [
                    { it.acquireAndExecute({ assertNever() }) },
                    { it.acquireAndExecute(Duration.ofHours(1), { assertNever() }) },
                    { it.acquireForeverAndExecute({ assertNever() }) },
            ]
    }

    def "should release the lock after action error"() {
        given:
            DistributedLockMock lock = releasedInMemoryLock()

        when:
            Boolean result = action(lock)
                    .onNotAcquired({ assertNever() })
                    .isAcquired()
        then:
            thrown(SpecSimulatedException)
            result == null
        and:
            lock.acquisitions() == 1
            lock.releases() == 1
        where:
            action << [
                    { it.acquireAndExecute({ throwSpecSimulatedException() }) },
                    { it.acquireAndExecute(Duration.ofHours(1), { throwSpecSimulatedException() }) },
                    { it.acquireForeverAndExecute({ throwSpecSimulatedException() }) },
            ]
    }

    def "should execute action on lock release"() {
        given:
            DistributedLockMock lock = acquiredInMemoryLock()
        when:
            boolean result = lock
                    .releaseAndExecute({ counter.increment() })
                    .onNotReleased({ assertNever() })
                    .isReleased()
        then:
            counter.value == 1
            result == true
        and:
            lock.acquisitions() == 0
            lock.releases() == 1
    }

    def "should not execute action when lock was not released"() {
        given:
            DistributedLockMock lock = releasedInMemoryLock()
        when:
            boolean result = lock
                    .releaseAndExecute({ assertNever() })
                    .onNotReleased({ counter.increment() })
                    .isReleased()
        then:
            counter.value == 1
            result == false
        and:
            lock.acquisitions() == 0
            lock.releases() == 1
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
