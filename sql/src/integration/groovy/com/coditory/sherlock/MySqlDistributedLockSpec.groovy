package com.coditory.sherlock

import com.coditory.sherlock.base.MySqlConnectionProvider

class MySqlReleaseLockSpec extends ReleaseLockSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlAcquireLockSpec extends AcquireLockSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}
