package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection
import java.time.Clock
import java.time.Duration

import static ReactorSqlSherlockBuilder.reactorSqlSherlock
import static com.coditory.sherlock.BlockingReactorSherlockWrapper.blockingReactorSherlock

trait UsesReactorSqlSherlock implements DistributedLocksCreator {
    static final String locksTableName = "locks"

    abstract ConnectionFactory getConnectionFactory()

    abstract Connection getBlockingConnection()

    abstract BindingMapper getBindingParameterMapper()

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        ReactorSherlock reactorLocks = reactorSqlSherlock()
                .withConnectionFactory(connectionFactory)
                .withLocksTable(locksTableName)
                .withBindingParameterMapper(bindingParameterMapper)
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingReactorSherlock(reactorLocks)
    }
}
