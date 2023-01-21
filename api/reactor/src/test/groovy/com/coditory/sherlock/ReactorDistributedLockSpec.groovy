package com.coditory.sherlock

import com.coditory.sherlock.base.SpecSimulatedException
import reactor.core.publisher.Mono
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static ReactorDistributedLockMock.acquiredInMemoryLock
import static ReactorDistributedLockMock.releasedInMemoryLock

class ReactorDistributedLockSpec extends Specification {
    @Shared
    Counter counter = new Counter()

    void setup() {
        counter.reset()
    }

    @Unroll
    def "should release the lock after executing the action"() {
        given:
            ReactorDistributedLockMock lock = releasedInMemoryLock("sample-lock")
        when:
            Integer result = action(lock).block()
        then:
            lock.acquisitions() == 1
            lock.releases() == 1
            counter.value == 1
            result == 1
        where:
            action << [
                    { it.acquireAndExecute(counter.incrementAndGet()) },
                    { it.acquireAndExecute(Duration.ofHours(1), counter.incrementAndGet()) },
                    { it.acquireForeverAndExecute(counter.incrementAndGet()) },
            ]
    }

    @Unroll
    def "should not execute action if lock was not acquired"() {
        given:
            ReactorDistributedLockMock lock = acquiredInMemoryLock("sample-lock")
        when:
            Integer result = action(lock).block()
        then:
            lock.acquisitions() == 1
            lock.releases() == 0
            counter.value == 0
            result == null
        where:
            action << [
                    { it.acquireAndExecute(counter.incrementAndGet()) },
                    { it.acquireAndExecute(Duration.ofHours(1), counter.incrementAndGet()) },
                    { it.acquireForeverAndExecute(counter.incrementAndGet()) },
            ]
    }

    @Unroll
    def "should release the lock after action error"() {
        given:
            ReactorDistributedLockMock lock = releasedInMemoryLock("sample-lock")
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
                    { it.acquireAndExecute(counter.incrementAndThrow()) },
                    { it.acquireAndExecute(Duration.ofHours(1), counter.incrementAndThrow()) },
                    { it.acquireForeverAndExecute(counter.incrementAndThrow()) },
            ]
    }

    def "should execute action on lock release"() {
        given:
            ReactorDistributedLockMock lock = acquiredInMemoryLock("sample-lock")
        when:
            Integer result = lock.releaseAndExecute(counter.incrementAndGet())
                    .block()
        then:
            lock.acquisitions() == 0
            lock.releases() == 1
            counter.value == 1
            result == 1
    }

    def "should not execute action when lock was not released"() {
        given:
            ReactorDistributedLockMock lock = releasedInMemoryLock("sample-lock")
        when:
            Integer result = lock.releaseAndExecute(counter.incrementAndGet())
                    .block()
        then:
            lock.acquisitions() == 0
            lock.releases() == 1
            counter.value == 0
            result == null
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
