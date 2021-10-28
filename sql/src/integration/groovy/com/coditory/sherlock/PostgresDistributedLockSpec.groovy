package com.coditory.sherlock

import com.coditory.sherlock.base.PostgresConnectionProvider

class PostgresReleaseLockSpec extends ReleaseLockSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresAcquireLockSpec extends AcquireLockSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}
