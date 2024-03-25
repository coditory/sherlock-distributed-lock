package com.coditory.sherlock.sql.reactor

import com.coditory.sherlock.sql.SqlIndexCreationSpec
import com.coditory.sherlock.sql.SqlLockStorageSpec

class ReactorPostgresLockStorageSpec extends SqlLockStorageSpec
    implements UsesReactorSqlSherlock, PostgresConnectionProvider {}

class ReactorPostgresIndexCreationSpec extends SqlIndexCreationSpec
    implements UsesReactorSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [tableName + "_pkey", tableName + "_idx"].sort()
}
