package com.coditory.sherlock

import com.coditory.sherlock.migrator.MigratorChangeSetsSpec
import com.coditory.sherlock.migrator.MigratorSpec
import com.coditory.sherlock.migrator.base.BlockingMigratorBuilder
import com.coditory.sherlock.migrator.base.BlockingSyncMigratorBuilder
import com.coditory.sherlock.migrator.base.MigratorCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.inmem.InMemorySherlockBuilder.inMemorySherlockBuilder

class SherlockMigratorSpec extends MigratorSpec implements UsesInMemorySherlock {}

class SherlockMigratorChangeSetSpec extends MigratorChangeSetsSpec implements UsesInMemorySherlock {
}

trait UsesInMemorySherlock implements MigratorCreator {
    @Override
    BlockingMigratorBuilder createMigratorBuilder(Sherlock sherlock) {
        return new BlockingSyncMigratorBuilder(sherlock)
    }

    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        return inMemorySherlockBuilder()
                .withOwnerId(ownerId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
    }
}