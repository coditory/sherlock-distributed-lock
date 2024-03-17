package com.coditory.sherlock.sql.rxjava

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.rxjava.RxSherlock
import com.coditory.sherlock.sql.BindingMapper
import io.r2dbc.spi.ConnectionFactory

import java.sql.Connection
import java.time.Clock
import java.time.Duration

import static com.coditory.sherlock.BlockingRxSherlockWrapper.blockingRxSherlock
import static RxSqlSherlockBuilder.rxSqlSherlock

trait UsesRxSqlSherlock implements DistributedLocksCreator {
    abstract ConnectionFactory getConnectionFactory()

    abstract Connection getBlockingConnection()

    abstract BindingMapper getBindingMapper()

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String tableName) {
        RxSherlock reactorLocks = rxSqlSherlock()
                .withConnectionFactory(connectionFactory)
                .withLocksTable(tableName)
                .withBindingMapper(bindingMapper)
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
        return blockingRxSherlock(reactorLocks)
    }
}
