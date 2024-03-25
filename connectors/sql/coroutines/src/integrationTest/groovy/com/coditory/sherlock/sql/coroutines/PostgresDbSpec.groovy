package com.coditory.sherlock.sql.coroutines

import com.coditory.sherlock.sql.SqlIndexCreationSpec
import com.coditory.sherlock.sql.SqlLockStorageSpec

class KtPostgresLockStorageSpec extends SqlLockStorageSpec
    implements UsesKtSqlSherlock, PostgresConnectionProvider {}

class KtPostgresIndexCreationSpec extends SqlIndexCreationSpec
    implements UsesKtSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [tableName + "_pkey", tableName + "_idx"].sort()
}
