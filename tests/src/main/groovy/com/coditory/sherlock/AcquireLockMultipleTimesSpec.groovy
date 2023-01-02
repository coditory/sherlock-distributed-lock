package com.coditory.sherlock

import com.coditory.sherlock.base.LockTypes
import spock.lang.Unroll

import java.time.Duration

import static com.coditory.sherlock.base.LockTypes.*

abstract class AcquireLockMultipleTimesSpec extends LocksBaseSpec {
    static List<LockTypes> mayAcquireMultipleTimes = [REENTRANT, OVERRIDING]

    @Unroll
    def "the same owner may acquire lock multiple times - #type"() {
        given:
            DistributedLock lock = createLock(type)
        when:
            boolean firstResult = lock.acquire()
            boolean secondResult = lock.acquire()
        then:
            firstResult == true
            secondResult == true
        where:
            type << mayAcquireMultipleTimes
    }

    @Unroll
    def "the same owner may acquire permanent lock multiple times - #type"() {
        given:
            DistributedLock lock = createPermanentLock(type)
        when:
            boolean firstResult = lock.acquire()
            boolean secondResult = lock.acquire()
        then:
            firstResult == true
            secondResult == true
        where:
            type << mayAcquireMultipleTimes
    }

    @Unroll
    def "the same instance may acquire lock only once - #type"() {
        given:
            DistributedLock lock = createLock(type)
        when:
            boolean firstResult = lock.acquire()
            boolean secondResult = lock.acquire()
        then:
            firstResult == true
            secondResult == false
        where:
            type << allLockTypes() - mayAcquireMultipleTimes
    }

    @Unroll
    def "the same instance may acquire lock multiple times by separate lock objects - #type"() {
        given:
            DistributedLock lock = createLock(type)
            DistributedLock otherObject = createLock(type)
        when:
            boolean firstResult = lock.acquire()
            boolean secondResult = otherObject.acquire()
        then:
            firstResult == true
            secondResult == true
        where:
            type << mayAcquireMultipleTimes
    }

    @Unroll
    def "only one of two different instances may acquire lock - #type"() {
        given:
            DistributedLock lock = createLock(type)
            DistributedLock otherLock = createLock(type)
        when:
            boolean firstResult = lock.acquire()
            boolean secondResult = otherLock.acquire()
        then:
            firstResult == true
        and:
            secondResult == false
        where:
            type << allLockTypes() - mayAcquireMultipleTimes
    }

    def "should prolong lock duration when acquired multiple times by reentrant lock"() {
        given:
            DistributedLock lock = createLock(REENTRANT)
        and:
            lock.acquire(Duration.ofHours(1))
            fixedClock.tick(Duration.ofMinutes(30))
        when:
            lock.acquire(Duration.ofHours(1))
        and:
            fixedClock.tick(Duration.ofMinutes(45))
        then:
            lock.release() == true
    }
}
