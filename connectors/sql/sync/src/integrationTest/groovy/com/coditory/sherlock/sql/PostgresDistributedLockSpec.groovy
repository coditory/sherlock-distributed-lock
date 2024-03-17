package com.coditory.sherlock.sql

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

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