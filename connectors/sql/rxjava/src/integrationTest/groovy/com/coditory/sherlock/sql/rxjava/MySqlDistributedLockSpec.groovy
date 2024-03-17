package com.coditory.sherlock.sql.rxjava

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class RxMySqlReleaseLockSpec extends ReleaseLockSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

class RxMySqlAcquireLockSpec extends AcquireLockSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

class RxMySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

class RxMySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

class RxMySqlHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}
