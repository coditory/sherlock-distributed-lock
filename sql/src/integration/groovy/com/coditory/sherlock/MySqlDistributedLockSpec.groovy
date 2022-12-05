package com.coditory.sherlock

class MySqlReleaseLockSpec extends ReleaseLockSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlAcquireLockSpec extends AcquireLockSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}