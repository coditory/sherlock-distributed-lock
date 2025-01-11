package com.coditory.sherlock.migrator

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.inmem.InMemorySherlock
import com.coditory.sherlock.migrator.base.BlockingMigratorBuilder
import com.coditory.sherlock.migrator.base.MigratorCreator

import java.time.Clock
import java.time.Duration

class SyncMigratorSpec extends MigratorSpec implements UsesInMemorySherlock {}

class SyncMigratorChangeSetSpec extends MigratorChangeSetsSpec implements UsesInMemorySherlock {
}

trait UsesInMemorySherlock implements MigratorCreator {
    @Override
    BlockingMigratorBuilder createMigratorBuilder(Sherlock sherlock) {
        return new BlockingSyncMigratorBuilder(sherlock)
    }

    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        return InMemorySherlock.builder()
            .withOwnerId(ownerId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
    }
}
