package com.coditory.sherlock

class KtPostgresLockStorageSpec extends SqlLockStorageSpec
        implements UsesKtSqlSherlock, PostgresConnectionProvider {}

class KtPostgresIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesKtSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [tableName + "_pkey", tableName + "_idx"].sort()
}
