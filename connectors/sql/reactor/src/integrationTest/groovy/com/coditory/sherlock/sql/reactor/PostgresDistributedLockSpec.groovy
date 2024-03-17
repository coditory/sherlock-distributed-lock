package com.coditory.sherlock.sql.reactor

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

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
