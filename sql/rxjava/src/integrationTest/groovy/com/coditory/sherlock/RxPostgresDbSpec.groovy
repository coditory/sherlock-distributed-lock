package com.coditory.sherlock

class RxPostgresLockStorageSpec extends SqlLockStorageSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {}

class RxPostgresIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [tableName + "_pkey", tableName + "_idx"].sort()
}
