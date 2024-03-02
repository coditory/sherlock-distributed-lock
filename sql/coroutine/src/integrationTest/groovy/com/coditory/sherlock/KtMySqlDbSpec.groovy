package com.coditory.sherlock

class KtMySqlLockStorageSpec extends SqlLockStorageSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

class KtMySqlIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", tableName + "_IDX"].sort()
}
