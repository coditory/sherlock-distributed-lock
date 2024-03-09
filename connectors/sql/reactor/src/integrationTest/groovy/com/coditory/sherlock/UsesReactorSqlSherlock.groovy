package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection
import java.time.Clock
import java.time.Duration

import static ReactorSqlSherlockBuilder.reactorSqlSherlock
import static com.coditory.sherlock.BlockingReactorSherlockWrapper.blockingReactorSherlock

trait UsesReactorSqlSherlock implements DistributedLocksCreator {
    abstract ConnectionFactory getConnectionFactory()

    abstract Connection getBlockingConnection()

    abstract BindingMapper getBindingMapper()

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String tableName) {
        ReactorSherlock reactorLocks = reactorSqlSherlock()
                .withConnectionFactory(connectionFactory)
                .withLocksTable(tableName)
                .withBindingMapper(bindingMapper)
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactorSherlock(reactorLocks)
    }
}
