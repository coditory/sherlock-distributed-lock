package com.coditory.sherlock

import com.coditory.sherlock.base.MySqlConnectionProvider
import com.coditory.sherlock.base.PostgresConnectionProvider

class PostgresReleaseLockSpec extends ReleaseLockSpec implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresAcquireLockSpec extends AcquireLockSpec implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesSqlSherlock, PostgresConnectionProvider {
}

class PostgresInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesSqlSherlock, PostgresConnectionProvider {
}

class MySqlReleaseLockSpec extends ReleaseLockSpec implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlAcquireLockSpec extends AcquireLockSpec implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesSqlSherlock, MySqlConnectionProvider {
}

class MySqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesSqlSherlock, MySqlConnectionProvider {
}
