package com.coditory.sherlock.coroutines

import com.coditory.sherlock.BlockingKtSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.coroutines.base.BlockingKtMigratorBuilder
import com.coditory.sherlock.migrator.MigratorChangeSetsSpec
import com.coditory.sherlock.migrator.MigratorSpec
import com.coditory.sherlock.migrator.base.BlockingMigratorBuilder
import com.coditory.sherlock.migrator.base.MigratorCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.inmem.coroutines.KtInMemorySherlockBuilder.coroutineInMemorySherlockBuilder

class KtMigratorSpec extends MigratorSpec implements UsesKtInMemorySherlock {}

class KtMigratorChangeSetSpec extends MigratorChangeSetsSpec implements UsesKtInMemorySherlock {}

trait UsesKtInMemorySherlock implements MigratorCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        KtSherlock ktLocks = coroutineInMemorySherlockBuilder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingKtSherlock(ktLocks)
    }

    @Override
    BlockingMigratorBuilder createMigratorBuilder(Sherlock sherlock) {
        if (sherlock instanceof BlockingKtSherlockWrapper) {
            return new BlockingKtMigratorBuilder(sherlock.unwrap())
        }
        throw new IllegalArgumentException("Expected ${BlockingKtSherlockWrapper.simpleName}, got: " + sherlock.class.simpleName)
    }
}