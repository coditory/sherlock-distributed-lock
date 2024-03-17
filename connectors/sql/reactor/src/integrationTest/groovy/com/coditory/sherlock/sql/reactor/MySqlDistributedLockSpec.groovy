package com.coditory.sherlock.sql.reactor

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

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
