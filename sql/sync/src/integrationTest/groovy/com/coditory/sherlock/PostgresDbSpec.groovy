package com.coditory.sherlock

class PostgresLockStorageSpec extends SqlLockStorageSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [tableName + "_pkey", tableName + "_idx"].sort()
}
