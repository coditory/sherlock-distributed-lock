package com.coditory.sherlock.sql.coroutines

import com.coditory.sherlock.sql.SqlIndexCreationSpec
import com.coditory.sherlock.sql.SqlLockStorageSpec

class KtMySqlLockStorageSpec extends SqlLockStorageSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {}

class KtMySqlIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesKtSqlSherlock, MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", SqlIndexCreationSpec.tableName + "_IDX"].sort()
}
