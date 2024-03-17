package com.coditory.sherlock.sql.rxjava

import com.coditory.sherlock.sql.SqlIndexCreationSpec
import com.coditory.sherlock.sql.SqlLockStorageSpec

class RxPostgresLockStorageSpec extends SqlLockStorageSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {}

class RxPostgresIndexCreationSpec extends SqlIndexCreationSpec
        implements UsesRxSqlSherlock, PostgresConnectionProvider {
    List<String> expectedIndexNames = [SqlIndexCreationSpec.tableName + "_pkey", SqlIndexCreationSpec.tableName + "_idx"].sort()
}
