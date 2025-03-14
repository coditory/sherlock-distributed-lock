package com.coditory.sherlock.rxjava

import com.coditory.sherlock.rxjava.test.SherlockStub
import spock.lang.Specification

import java.time.Duration

import static com.coditory.sherlock.rxjava.test.DistributedLockMock.lockStub

class SherlockStubSpec extends Specification {
    def "should create sherlock returning always opened locks"() {
        given:
            String lockId = "some-lock"
            Sherlock sherlock = SherlockStub.withReleasedLocks()

        expect:
            assertAlwaysOpenedLock(sherlock.createLock(lockId), lockId)
            assertAlwaysOpenedLock(sherlock.createReentrantLock(lockId), lockId)
            assertAlwaysOpenedLock(sherlock.createOverridingLock(lockId), lockId)
    }

    def "should create sherlock returning always closed locks"() {
        given:
            String lockId = "some-lock"
            Sherlock sherlock = SherlockStub.withAcquiredLocks()

        expect:
            assertAlwaysClosedLock(sherlock.createLock(lockId), lockId)
            assertAlwaysClosedLock(sherlock.createReentrantLock(lockId), lockId)
            assertAlwaysClosedLock(sherlock.createOverridingLock(lockId), lockId)
    }

    def "should create sherlock returning closed locks by default and opened lock for specific id"() {
        given:
            String lockId = "some-lock"
            Sherlock sherlock = SherlockStub.withAcquiredLocks()
                .withLock(lockStub(lockId, true))

        expect:
            assertAlwaysClosedLock(sherlock.createLock("other-lock"))
            assertAlwaysOpenedLock(sherlock.createLock(lockId))
    }

    static assertAlwaysOpenedLock(DistributedLock lock, String lockId = lock.id) {
        assertSingleStateLock(lock, lockId, true)
    }

    static assertAlwaysClosedLock(DistributedLock lock, String lockId = lock.id) {
        assertSingleStateLock(lock, lockId, false)
    }

    private static assertSingleStateLock(DistributedLock lock, String lockId, boolean expectedResult) {
        assert lock.id == lockId
        assert lock.acquire().blockingGet().acquired == expectedResult
        assert lock.acquire(Duration.ofHours(1)).blockingGet().acquired == expectedResult
        assert lock.acquireForever().blockingGet().acquired == expectedResult
        assert lock.release().blockingGet().released == expectedResult
        return true
    }
}
