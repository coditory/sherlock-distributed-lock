package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.coroutines.KtSherlock
import com.coditory.sherlock.sql.BindingMapper
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection
import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.sql.coroutines.KtSqlSherlockBuilder.coroutineSqlSherlock

trait UsesKtSqlSherlock implements DistributedLocksCreator {
    abstract ConnectionFactory getConnectionFactory()

    abstract Connection getBlockingConnection()

    abstract BindingMapper getBindingMapper()

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String tableName) {
        KtSherlock coroutineSqlSherlock = coroutineSqlSherlock()
                .withConnectionFactory(connectionFactory)
                .withBindingMapper(bindingMapper)
                .withLocksTable(tableName)
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return BlockingKtSherlockWrapper.blockingKtSherlock(coroutineSqlSherlock)
    }
}
