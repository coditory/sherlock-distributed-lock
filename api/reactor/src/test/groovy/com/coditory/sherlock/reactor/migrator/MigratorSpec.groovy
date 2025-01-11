package com.coditory.sherlock.reactor.migrator

import com.coditory.sherlock.BlockingReactorSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.migrator.MigratorChangeSetsSpec
import com.coditory.sherlock.migrator.MigratorSpec
import com.coditory.sherlock.migrator.base.BlockingMigratorBuilder
import com.coditory.sherlock.migrator.base.MigratorCreator

import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.inmem.reactor.InMemorySherlock.builder

class ReactorMigratorSpec extends MigratorSpec implements UsesReactorInMemorySherlock {}

class ReactorMigratorChangeSetSpec extends MigratorChangeSetsSpec implements UsesReactorInMemorySherlock {}

trait UsesReactorInMemorySherlock implements MigratorCreator {
    @Override
    Sherlock createSherlock(String ownerId, Duration duration, Clock clock, String collectionName) {
        com.coditory.sherlock.reactor.Sherlock reactorLocks = builder()
            .withOwnerId(ownerId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
        return new BlockingReactorSherlockWrapper(reactorLocks)
    }

    @Override
    BlockingMigratorBuilder createMigratorBuilder(Sherlock sherlock) {
        if (sherlock instanceof BlockingReactorSherlockWrapper) {
            return new BlockingReactorMigratorBuilder(sherlock.unwrap())
        }
        throw new IllegalArgumentException("Expected ${BlockingReactorMigratorBuilder.simpleName}, got: " + sherlock.class.simpleName)
    }
}
