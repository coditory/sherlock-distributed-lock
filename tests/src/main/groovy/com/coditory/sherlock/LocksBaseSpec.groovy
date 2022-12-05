package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.base.LockTypes
import com.coditory.sherlock.base.UpdatableFixedClock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

import java.time.Duration

import static com.coditory.sherlock.LockDuration.permanent
import static com.coditory.sherlock.base.UpdatableFixedClock.defaultUpdatableFixedClock

abstract class LocksBaseSpec extends Specification
        implements DistributedLocksCreator {
    static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()
    static final Duration defaultLockDuration = Duration.ofMinutes(10)
    static final String sampleOwnerId = "locks_test_instance"
    static final String sampleLockId = "sample_acquire_id"
    private static Logger logger = LoggerFactory.getLogger(LocksBaseSpec)
    Sherlock sherlock

    void setup() {
        sherlock = createSherlock()
    }

    void cleanup() {
        try {
            fixedClock.reset()
            createSherlock().forceReleaseAllLocks()
        } catch (Throwable e) {
            logger.warn("Cleanup exception: " + e.getMessage())
        }
    }

    DistributedLock createLock(
            LockTypes type,
            String lockId = sampleLockId,
            String ownerId = sampleOwnerId,
            Duration duration = defaultLockDuration) {
        return type.createLock(sherlock)
                .withLockId(lockId)
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .build()
    }

    DistributedLock createPermanentLock(
            LockTypes type,
            String lockId = sampleLockId,
            String ownerId = sampleOwnerId) {
        return type.createLock(sherlock)
                .withLockId(lockId)
                .withOwnerId(ownerId)
                .withLockDuration(permanent())
                .build()
    }

    Sherlock createSherlock(String ownerId = sampleOwnerId, Duration duration = defaultLockDuration) {
        return createSherlock(ownerId, duration, fixedClock)
    }
}

