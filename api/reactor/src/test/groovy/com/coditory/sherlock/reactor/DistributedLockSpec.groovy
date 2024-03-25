package com.coditory.sherlock.reactor

import com.coditory.sherlock.base.SpecSimulatedException
import com.coditory.sherlock.reactor.test.DistributedLockMock
import reactor.core.publisher.Mono
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.reactor.test.DistributedLockMock.acquiredInMemoryLock
import static com.coditory.sherlock.reactor.test.DistributedLockMock.releasedInMemoryLock

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
            Integer result = action(lock).block()
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
            Integer result = action(lock).block()
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
            Integer result = action(lock).block()
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

        Mono<Integer> incrementAndGet() {
            return Mono.fromCallable({ ++value })
        }

        Mono<Integer> incrementAndThrow() {
            return Mono.fromCallable({ ++value })
                .then(Mono.error(new SpecSimulatedException()))
        }
    }
}
