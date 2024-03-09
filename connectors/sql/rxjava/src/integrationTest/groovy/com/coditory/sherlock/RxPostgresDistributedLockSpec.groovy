package com.coditory.sherlock

class RxPostgresReleaseLockSpec extends ReleaseLockSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {}

class RxPostgresAcquireLockSpec extends AcquireLockSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {}

class RxPostgresAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {}

class RxPostgresInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {}

class RxPostgresHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {}
