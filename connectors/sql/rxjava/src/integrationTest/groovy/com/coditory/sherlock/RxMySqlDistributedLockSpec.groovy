package com.coditory.sherlock

import spock.lang.Ignore

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
