package com.coditory.sherlock

import com.coditory.sherlock.base.SpecSimulatedException
import org.junit.Before
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.DistributedLockMock.alwaysClosedLock
import static com.coditory.sherlock.DistributedLockMock.alwaysOpenedLock

class DistributedLockSpec extends Specification {
  @Shared
  Counter counter = new Counter()

  @Before
  void resetCounter() {
    counter.reset()
  }

  @Unroll
  def "should execute action and release the lock"() {
    given:
      DistributedLockMock lock = alwaysOpenedLock("sample-lock")

    when:
      boolean result = action(lock)
    then:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 1
      counter.value == 1
      result == true

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
      DistributedLockMock lock = alwaysClosedLock("sample-lock")

    when:
      boolean result = action(lock)
    then:
      lock.acquireInvocations() == 1
      lock.releaseInvocations() == 0
      counter.value == 0
      result == false

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
      DistributedLockMock lock = alwaysOpenedLock("sample-lock")

    when:
      Boolean result = action(lock)
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
      DistributedLockMock lock = alwaysOpenedLock("sample-lock")

    when:
      boolean result = lock.releaseAndExecute({ counter.incrementAndGet() })
    then:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 1
      counter.value == 1
      result == true
  }

  def "should not execute action when lock was not released"() {
    given:
      DistributedLockMock lock = alwaysOpenedLock("sample-lock")

    when:
      boolean result = lock.releaseAndExecute({ counter.incrementAndThrow() })
    then:
      thrown(SpecSimulatedException)
    and:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 1
      counter.value == 1
      result == false
  }

  class Counter {
    private int value = 0

    void reset() {
      value = 0
    }

    int incrementAndGet() {
      return ++value
    }

    void incrementAndThrow() {
      value++
      throw new SpecSimulatedException()
    }
  }
}
