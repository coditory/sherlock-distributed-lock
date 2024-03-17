package com.coditory.sherlock.rxjava


import spock.lang.Specification

import java.time.Duration

import static com.coditory.sherlock.rxjava.RxDistributedLockMock.lockStub

class RxSherlockStubSpec extends Specification {
    def "should create sherlock returning always opened locks"() {
        given:
            String lockId = "some-lock"
            RxSherlock sherlock = RxSherlockStub.withReleasedLocks()

        expect:
            assertAlwaysOpenedLock(sherlock.createLock(lockId), lockId)
            assertAlwaysOpenedLock(sherlock.createReentrantLock(lockId), lockId)
            assertAlwaysOpenedLock(sherlock.createOverridingLock(lockId), lockId)
    }

    def "should create sherlock returning always closed locks"() {
        given:
            String lockId = "some-lock"
            RxSherlock sherlock = RxSherlockStub.withAcquiredLocks()

        expect:
            assertAlwaysClosedLock(sherlock.createLock(lockId), lockId)
            assertAlwaysClosedLock(sherlock.createReentrantLock(lockId), lockId)
            assertAlwaysClosedLock(sherlock.createOverridingLock(lockId), lockId)
    }

    def "should create sherlock returning closed locks by default and opened lock for specific id"() {
        given:
            String lockId = "some-lock"
            RxSherlock sherlock = RxSherlockStub.withAcquiredLocks()
                    .withLock(lockStub(lockId, true))

        expect:
            assertAlwaysClosedLock(sherlock.createLock("other-lock"))
            assertAlwaysOpenedLock(sherlock.createLock(lockId))
    }

    static assertAlwaysOpenedLock(RxDistributedLock lock, String lockId = lock.id) {
        assertSingleStateLock(lock, lockId, true)
    }

    static assertAlwaysClosedLock(RxDistributedLock lock, String lockId = lock.id) {
        assertSingleStateLock(lock, lockId, false)
    }

    private static assertSingleStateLock(RxDistributedLock lock, String lockId, boolean expectedResult) {
        assert lock.id == lockId
        assert lock.acquire().blockingGet().acquired == expectedResult
        assert lock.acquire(Duration.ofHours(1)).blockingGet().acquired == expectedResult
        assert lock.acquireForever().blockingGet().acquired == expectedResult
        assert lock.release().blockingGet().released == expectedResult
        return true
    }
}
