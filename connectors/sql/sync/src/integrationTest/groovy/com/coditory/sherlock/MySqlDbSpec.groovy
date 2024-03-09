package com.coditory.sherlock

class MySqlLockStorageSpec extends SqlLockStorageSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", tableName + "_IDX"].sort()
}
