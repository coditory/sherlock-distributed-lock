package com.coditory.sherlock.sql.coroutines

import com.coditory.sherlock.sql.SqlIndexCreationSpec
import com.coditory.sherlock.sql.SqlLockStorageSpec

class ReactorPostgresLockStorageSpec extends SqlLockStorageSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {}

class ReactorPostgresIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesReactorSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [SqlIndexCreationSpec.tableName + "_pkey", SqlIndexCreationSpec.tableName + "_idx"].sort()
}
