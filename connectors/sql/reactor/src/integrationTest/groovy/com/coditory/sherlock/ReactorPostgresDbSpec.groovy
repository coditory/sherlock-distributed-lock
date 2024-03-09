package com.coditory.sherlock

class ReactorPostgresLockStorageSpec extends SqlLockStorageSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {}

class ReactorPostgresIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [tableName + "_pkey", tableName + "_idx"].sort()
}
