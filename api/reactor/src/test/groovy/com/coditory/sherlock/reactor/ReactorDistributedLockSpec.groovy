package com.coditory.sherlock.reactor

import com.coditory.sherlock.reactor.base.SpecSimulatedException
import org.junit.Before
import reactor.core.publisher.Mono
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.reactor.ReactorDistributedLockMock.alwaysClosedLock
import static com.coditory.sherlock.reactor.ReactorDistributedLockMock.alwaysOpenedLock

class ReactorDistributedLockSpec extends Specification {
  @Shared
  Counter counter = new Counter()

  @Before
  void resetCounter() {
    counter.reset()
  }

  @Unroll
  def "should execute action and release the lock"() {
    given:
      ReactorDistributedLockMock lock = alwaysOpenedLock("sample-lock")

    when:
      Integer result = action(lock).block()
    then:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 1
      counter.value == 1
      result == 1

    where:
      action << [
          { it.acquireAndExecute({ counter.incrementAndGet() }) },
          { it.acquireAndExecute(Duration.ofHours(1), { counter.incrementAndGet() }) },
          { it.acquireForeverAndExecute({ counter.incrementAndGet() }) },
      ]
  }

  @Unroll
  def "should not execute action if lock was not acquired"() {
    given:
      ReactorDistributedLockMock lock = alwaysClosedLock("sample-lock")

    when:
      Integer result = action(lock).block()
    then:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 0
      counter.value == 0
      result == null

    where:
      action << [
          { it.acquireAndExecute({ counter.incrementAndGet() }) },
          { it.acquireAndExecute(Duration.ofHours(1), { counter.incrementAndGet() }) },
          { it.acquireForeverAndExecute({ counter.incrementAndGet() }) },
      ]
  }

  @Unroll
  def "should execute action and release the lock on error"() {
    given:
      ReactorDistributedLockMock lock = alwaysOpenedLock("sample-lock")

    when:
      Integer result = action(lock).block()
    then:
      thrown(SpecSimulatedException)
    and:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 1
      counter.value == 1
      result == null

    where:
      action << [
          { it.acquireAndExecute({ counter.incrementAndThrow() }) },
          { it.acquireAndExecute(Duration.ofHours(1), { counter.incrementAndThrow() }) },
          { it.acquireForeverAndExecute({ counter.incrementAndThrow() }) },
      ]
  }

  def "should execute action on lock release"() {
    given:
      ReactorDistributedLockMock lock = alwaysOpenedLock("sample-lock")

    when:
      Integer result = lock.releaseAndExecute({ counter.incrementAndGet() })
          .block()
    then:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 1
      counter.value == 1
      result == 1
  }

  def "should not execute action when lock was not released"() {
    given:
      ReactorDistributedLockMock lock = alwaysOpenedLock("sample-lock")

    when:
      Integer result = lock.releaseAndExecute({ counter.incrementAndThrow() })
        .block()
    then:
      thrown(SpecSimulatedException)
    and:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 1
      counter.value == 1
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
