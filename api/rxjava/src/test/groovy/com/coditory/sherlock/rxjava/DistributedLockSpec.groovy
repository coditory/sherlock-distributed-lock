package com.coditory.sherlock.rxjava

import com.coditory.sherlock.base.SpecSimulatedException
import com.coditory.sherlock.rxjava.test.DistributedLockMock
import io.reactivex.Single
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.rxjava.test.DistributedLockMock.acquiredInMemoryLock
import static com.coditory.sherlock.rxjava.test.DistributedLockMock.releasedInMemoryLock

class DistributedLockSpec extends Specification {
    @Shared
    Counter counter = new Counter()

    void setup() {
        counter.reset()
    }

    @Unroll
    def "should release the lock after executing the action"() {
        given:
            DistributedLockMock lock = releasedInMemoryLock("sample-lock")
        when:
            Integer result = action(lock).blockingGet()
        then:
            lock.acquisitions() == 1
            lock.releases() == 1
            counter.value == 1
            result == 1
        where:
            action << [
                { it.runLocked(counter.incrementAndGet()) },
                { it.runLocked(Duration.ofHours(1), counter.incrementAndGet()) },
            ]
    }

    @Unroll
    def "should not execute action if lock was not acquired"() {
        given:
            DistributedLockMock lock = acquiredInMemoryLock("sample-lock")
        when:
            Integer result = action(lock).blockingGet()
        then:
            lock.acquisitions() == 1
            lock.releases() == 0
            counter.value == 0
            result == null
        where:
            action << [
                { it.runLocked(counter.incrementAndGet()) },
                { it.runLocked(Duration.ofHours(1), counter.incrementAndGet()) },
            ]
    }

    @Unroll
    def "should release the lock after action error"() {
        given:
            DistributedLockMock lock = releasedInMemoryLock("sample-lock")
        when:
            Integer result = action(lock).blockingGet()
        then:
            thrown(SpecSimulatedException)
        and:
            lock.acquisitions() == 1
            lock.releases() == 1
            counter.value == 1
            result == null
        where:
            action << [
                { it.runLocked(counter.incrementAndThrow()) },
                { it.runLocked(Duration.ofHours(1), counter.incrementAndThrow()) },
            ]
    }

    class Counter {
        private int value

        void reset() {
            value = 0
        }

        Single<Integer> incrementAndGet() {
            return Single.fromCallable({ ++value })
        }

        Single<Integer> incrementAndThrow() {
            return Single.fromCallable({ ++value })
                .flatMap({ Single.<Integer> error(new SpecSimulatedException()) })
        }
    }
}
