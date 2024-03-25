package com.coditory.sherlock.sql.rxjava

import com.coditory.sherlock.BlockingRxSherlockWrapper
import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.sql.BindingMapper
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection
import java.time.Clock
import java.time.Duration

trait UsesRxSqlSherlock implements DistributedLocksCreator {
    abstract ConnectionFactory getConnectionFactory()

    abstract Connection getBlockingConnection()

    abstract BindingMapper getBindingMapper()

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String tableName) {
        com.coditory.sherlock.rxjava.Sherlock reactorLocks = SqlSherlock.builder()
            .withConnectionFactory(connectionFactory)
            .withLocksTable(tableName)
            .withBindingMapper(bindingMapper)
            .withOwnerId(instanceId)
            .withLockDuration(duration)
            .withClock(clock)
            .build()
        return new BlockingRxSherlockWrapper(reactorLocks)
    }
}
