package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection
import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingRxSherlockWrapper.blockingRxSherlock
import static com.coditory.sherlock.RxSqlSherlockBuilder.rxSqlSherlock

trait UsesRxSqlSherlock implements DistributedLocksCreator {
    static final String locksTableName = "locks"

    abstract ConnectionFactory getConnectionFactory()

    abstract Connection getBlockingConnection()

    abstract BindingMapper getBindingParameterMapper()

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock) {
        RxSherlock reactorLocks = rxSqlSherlock()
                .withConnectionFactory(connectionFactory)
                .withLocksTable(locksTableName)
                .withBindingParameterMapper(bindingParameterMapper)
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingRxSherlock(reactorLocks)
    }
}
