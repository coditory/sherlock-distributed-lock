package com.coditory.sherlock

import spock.lang.Ignore

// MySQL support through the official MySQL connector will be available when it supports r2dbc v1.0.0
// Sources:
// R2DBC official driver list: https://r2dbc.io/drivers/
// MySQL connector: https://github.com/mirromutth/r2dbc-mysql
@Ignore
class RxMySqlReleaseLockSpec extends ReleaseLockSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

@Ignore
class RxMySqlAcquireLockSpec extends AcquireLockSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

@Ignore
class RxMySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

@Ignore
class RxMySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

@Ignore
class RxMySqlHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}
