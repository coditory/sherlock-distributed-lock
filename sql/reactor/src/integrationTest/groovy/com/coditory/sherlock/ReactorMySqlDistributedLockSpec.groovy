package com.coditory.sherlock

import spock.lang.Ignore

// MySQL support through the official MySQL connector will be available when it supports r2dbc v1.0.0
// Sources:
// R2DBC official driver list: https://r2dbc.io/drivers/
// MySQL connector: https://github.com/mirromutth/r2dbc-mysql
@Ignore
class ReactorMySqlReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

@Ignore
class ReactorMySqlAcquireLockSpec extends AcquireLockSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

@Ignore
class ReactorMySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

@Ignore
class ReactorMySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

@Ignore
class ReactorMySqlHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}
