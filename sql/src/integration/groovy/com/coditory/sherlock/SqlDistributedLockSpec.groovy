package com.coditory.sherlock

class SqlReleaseLockSpec extends ReleaseLockSpec implements UsesSqlSherlock {}
class SqlAcquireLockSpec extends AcquireLockSpec implements UsesSqlSherlock {}
class SqlAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesSqlSherlock {}
class SqlInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesSqlSherlock {}
