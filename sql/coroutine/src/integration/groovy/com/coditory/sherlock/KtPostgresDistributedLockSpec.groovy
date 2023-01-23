package com.coditory.sherlock

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
