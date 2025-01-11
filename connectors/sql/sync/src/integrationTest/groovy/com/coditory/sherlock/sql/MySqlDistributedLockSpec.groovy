package com.coditory.sherlock.sql

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

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

class MySqlLockCommitSpec extends SqlLockCommitSpec
    implements UsesSqlSherlock, MySqlConnectionProvider {}
