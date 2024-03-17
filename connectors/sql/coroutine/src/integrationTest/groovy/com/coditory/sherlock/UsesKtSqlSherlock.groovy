package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.sql.BindingMapper
import com.coditory.sherlock.sql.coroutines.SqlSherlock
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection
import java.time.Clock
import java.time.Duration

trait UsesKtSqlSherlock implements DistributedLocksCreator {
    abstract ConnectionFactory getConnectionFactory()

    abstract Connection getBlockingConnection()

    abstract BindingMapper getBindingMapper()

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String tableName) {
        com.coditory.sherlock.coroutines.Sherlock coroutineSqlSherlock = SqlSherlock.builder()
                .withConnectionFactory(connectionFactory)
                .withBindingMapper(bindingMapper)
                .withLocksTable(tableName)
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return new BlockingKtSherlockWrapper(coroutineSqlSherlock)
    }
}
