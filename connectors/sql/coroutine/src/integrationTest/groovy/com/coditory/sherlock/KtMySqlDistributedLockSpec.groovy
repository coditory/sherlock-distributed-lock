package com.coditory.sherlock

class MySqlReleaseLockSpec extends ReleaseLockSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

class MySqlAcquireLockSpec extends AcquireLockSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

class MySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

class MySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

class MySqlHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}
