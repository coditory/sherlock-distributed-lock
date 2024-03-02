package com.coditory.sherlock

class ReactorMySqlLockStorageSpec extends SqlLockStorageSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

class ReactorMySqlIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesReactorSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", tableName + "_IDX"].sort()
}
