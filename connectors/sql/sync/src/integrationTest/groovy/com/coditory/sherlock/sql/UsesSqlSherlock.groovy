package com.coditory.sherlock.sql


import com.coditory.sherlock.Sherlock

import javax.sql.DataSource
import java.time.Clock
import java.time.Duration

import static SqlSherlockBuilder.sqlSherlock

trait UsesSqlSherlock implements SqlDistributedLocksCreator {
    abstract DataSource getDataSource()

    abstract DataSource getDataSource(DataSourceConfigurer configurer)

    @Override
    Sherlock createSherlock(String instanceId, Duration duration, Clock clock, String tableName) {
        return sqlSherlock()
                .withDataSource(dataSource)
                .withLocksTable(tableName)
                .withOwnerId(instanceId)
                .withLockDuration(duration)
                .withClock(clock)
                .build()
    }

    @Override
    Sherlock createSherlock(DataSourceConfigurer configurer) {
        return sqlSherlock()
                .withDataSource(getDataSource(configurer))
                .build()
    }
}
