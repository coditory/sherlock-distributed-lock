package com.coditory.sherlock.sql.coroutines

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.reactor.ReactorSherlock
import com.coditory.sherlock.sql.BindingMapper
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection
import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingReactorSherlockWrapper.blockingReactorSherlock
import static com.coditory.sherlock.sql.coroutines.ReactorSqlSherlockBuilder.reactorSqlSherlock

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
