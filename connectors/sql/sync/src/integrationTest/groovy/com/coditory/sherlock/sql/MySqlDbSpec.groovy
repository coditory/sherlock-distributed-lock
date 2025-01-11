package com.coditory.sherlock.sql

class MySqlLockStorageSpec extends SqlLockStorageSpec
    implements UsesSqlSherlock, MySqlConnectionProvider {}

class MySqlIndexCreationSpec extends SqlIndexCreationSpec
    implements UsesSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", SqlIndexCreationSpec.tableName + "_IDX"].sort()
}
