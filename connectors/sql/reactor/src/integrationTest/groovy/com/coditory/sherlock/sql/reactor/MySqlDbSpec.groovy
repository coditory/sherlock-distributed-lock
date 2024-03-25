package com.coditory.sherlock.sql.reactor

import com.coditory.sherlock.sql.SqlIndexCreationSpec
import com.coditory.sherlock.sql.SqlLockStorageSpec

class ReactorMySqlLockStorageSpec extends SqlLockStorageSpec
    implements UsesReactorSqlSherlock, MySqlConnectionProvider {}

class ReactorMySqlIndexCreationSpec extends SqlIndexCreationSpec
    implements UsesReactorSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", tableName + "_IDX"].sort()
}
