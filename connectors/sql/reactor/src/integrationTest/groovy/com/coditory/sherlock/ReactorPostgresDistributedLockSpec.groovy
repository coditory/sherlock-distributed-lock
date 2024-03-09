package com.coditory.sherlock

class ReactorPostgresReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {}

class ReactorPostgresAcquireLockSpec extends AcquireLockSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {}

class ReactorPostgresAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {}

class ReactorPostgresInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {}

class ReactorPostgresHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {}
