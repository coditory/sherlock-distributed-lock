package com.coditory.sherlock

import com.coditory.sherlock.sql.SqlIndexCreationSpec
import com.coditory.sherlock.sql.SqlLockStorageSpec

class KtMySqlLockStorageSpec extends SqlLockStorageSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

class KtMySqlIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", tableName + "_IDX"].sort()
}
