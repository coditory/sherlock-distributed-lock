package com.coditory.sherlock.rxjava

import com.coditory.sherlock.rxjava.base.SpecSimulatedException
import com.coditory.sherlock.rxjava.test.RxJavaDistributedLockMock
import io.reactivex.Single
import org.junit.Before
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.rxjava.test.RxJavaDistributedLockMock.alwaysAcquiredLock
import static com.coditory.sherlock.rxjava.test.RxJavaDistributedLockMock.alwaysReleasedLock

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
      RxJavaDistributedLockMock lock = alwaysReleasedLock("sample-lock")

    when:
      Integer result = action(lock).blockingGet()
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
      RxJavaDistributedLockMock lock = alwaysAcquiredLock("sample-lock")

    when:
      Integer result = action(lock).blockingGet()
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
      RxJavaDistributedLockMock lock = alwaysReleasedLock("sample-lock")

    when:
      Integer result = action(lock).blockingGet()
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
      RxJavaDistributedLockMock lock = alwaysReleasedLock("sample-lock")

    when:
      Integer result = lock.releaseAndExecute({ counter.incrementAndGet() })
          .blockingGet()
    then:
      lock.acquireInvocations() == 0
      lock.releaseInvocations() == 1
      counter.value == 1
      result == 1
  }

  def "should not execute action when lock was not released"() {
    given:
      RxJavaDistributedLockMock lock = alwaysReleasedLock("sample-lock")

    when:
      Integer result = lock.releaseAndExecute({ counter.incrementAndThrow() })
          .blockingGet()
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

    Single<Integer> incrementAndGet() {
      return Single.fromCallable({ ++value })
    }

    Single<Integer> incrementAndThrow() {
      return Single.fromCallable({ ++value })
          .flatMap { Single.error(new SpecSimulatedException()) }
    }
  }
}
