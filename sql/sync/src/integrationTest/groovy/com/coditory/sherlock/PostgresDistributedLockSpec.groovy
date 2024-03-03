package com.coditory.sherlock

class PostgresReleaseLockSpec extends ReleaseLockSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresAcquireLockSpec extends AcquireLockSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresSqlLockCommitSpec extends SqlLockCommitSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}