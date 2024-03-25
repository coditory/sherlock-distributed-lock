package com.coditory.sherlock.sql.coroutines

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class PostgresReleaseLockSpec extends ReleaseLockSpec
    implements UsesKtSqlSherlock, PostgresConnectionProvider {}

class PostgresAcquireLockSpec extends AcquireLockSpec
    implements UsesKtSqlSherlock, PostgresConnectionProvider {}

class PostgresAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
    implements UsesKtSqlSherlock, PostgresConnectionProvider {}

class PostgresInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
    implements UsesKtSqlSherlock, PostgresConnectionProvider {}

class PostgresHandleDbFailureSpec extends HandleDbFailureSpec
    implements UsesKtSqlSherlock, PostgresConnectionProvider {}
