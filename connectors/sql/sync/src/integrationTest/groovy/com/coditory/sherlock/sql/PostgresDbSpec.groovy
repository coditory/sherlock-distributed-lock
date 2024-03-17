package com.coditory.sherlock.sql

class PostgresLockStorageSpec extends SqlLockStorageSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {}

class PostgresIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [SqlIndexCreationSpec.tableName + "_pkey", SqlIndexCreationSpec.tableName + "_idx"].sort()
}
