package com.coditory.sherlock.sql.rxjava

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

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
