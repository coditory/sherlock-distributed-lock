package com.coditory.sherlock.sql.coroutines

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

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
