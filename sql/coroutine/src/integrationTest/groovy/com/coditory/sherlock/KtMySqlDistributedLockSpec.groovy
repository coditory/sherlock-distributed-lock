package com.coditory.sherlock

import spock.lang.Ignore

// MySQL support through the official MySQL connector will be available when it supports r2dbc v1.0.0
// Sources:
// R2DBC official driver list: https://r2dbc.io/drivers/
// MySQL connector: https://github.com/mirromutth/r2dbc-mysql
@Ignore
class MySqlReleaseLockSpec extends ReleaseLockSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

@Ignore
class MySqlAcquireLockSpec extends AcquireLockSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

@Ignore
class MySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

@Ignore
class MySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

@Ignore
class MySqlHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}
