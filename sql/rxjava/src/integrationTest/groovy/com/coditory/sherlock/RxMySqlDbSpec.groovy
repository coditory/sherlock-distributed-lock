package com.coditory.sherlock

class RxMySqlLockStorageSpec extends SqlLockStorageSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

class RxMySqlIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", tableName + "_IDX"].sort()
}
