package com.coditory.sherlock.sql.rxjava

import com.coditory.sherlock.sql.SqlIndexCreationSpec
import com.coditory.sherlock.sql.SqlLockStorageSpec

class RxMySqlLockStorageSpec extends SqlLockStorageSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {}

class RxMySqlIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesRxSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", SqlIndexCreationSpec.tableName + "_IDX"].sort()
}
