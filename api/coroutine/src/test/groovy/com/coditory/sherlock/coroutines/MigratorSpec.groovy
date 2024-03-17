package com.coditory.sherlock.coroutines

import com.coditory.sherlock.BlockingKtSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.coroutines.base.BlockingKtMigratorBuilder
import com.coditory.sherlock.inmem.coroutines.InMemorySherlock
import com.coditory.sherlock.migrator.MigratorChangeSetsSpec
import com.coditory.sherlock.migrator.MigratorSpec
import com.coditory.sherlock.migrator.base.BlockingMigratorBuilder
import com.coditory.sherlock.migrator.base.MigratorCreator

import java.time.Clock
import java.time.Duration

class CoroutinesMigratorSpec extends MigratorSpec implements UsesKtInMemorySherlock {}

class CoroutinesMigratorChangeSetSpec extends MigratorChangeSetsSpec implements UsesKtInMemorySherlock {}

trait UsesKtInMemorySherlock implements MigratorCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.coroutines.Sherlock sherlock = InMemorySherlock.builder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return new BlockingKtSherlockWrapper(sherlock)
    }

    @Override
    BlockingMigratorBuilder createMigratorBuilder(Sherlock sherlock) {
        if (sherlock instanceof BlockingKtSherlockWrapper) {
            return new BlockingKtMigratorBuilder(sherlock.unwrap())
        }
        throw new IllegalArgumentException("Expected ${BlockingKtSherlockWrapper.simpleName}, got: " + sherlock.class.simpleName)
    }
}