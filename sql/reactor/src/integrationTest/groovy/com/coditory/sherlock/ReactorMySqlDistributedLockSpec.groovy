package com.coditory.sherlock

class ReactorMySqlReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

class ReactorMySqlAcquireLockSpec extends AcquireLockSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

class ReactorMySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

class ReactorMySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

class ReactorMySqlHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}
