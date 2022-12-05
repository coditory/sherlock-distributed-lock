package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.MySqlConnectionProvider
import com.coditory.sherlock.PostgresConnectionProvider
import com.coditory.sherlock.Sherlock
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.Connection
import java.sql.Statement

import static com.coditory.sherlock.SqlSherlockBuilder.sqlSherlock
import static com.coditory.sherlock.infrastructure.SqlTableIndexes.listTableIndexes

class PostgresIndexCreationSpec extends SqlIndexCreationSpec implements PostgresConnectionProvider {
    List<String> expectedIndexNames = [tableName + "_pkey"]
}

class MySqlIndexCreationSpec extends SqlIndexCreationSpec implements MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY"]
}

abstract class SqlIndexCreationSpec extends Specification {
    String tableName = "other_locks"
    Sherlock locks = sqlSherlock()
            .withConnectionPool(connectionPool)
            .withLocksTable(tableName)
            .build()

    abstract List<String> getExpectedIndexNames()

    abstract DataSource getConnectionPool();

    void cleanup() {
        try (
                Connection connection = connectionPool.getConnection()
                Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate("DROP TABLE " + tableName)
        }
    }

    def "should create sql indexes on initialize"() {
        expect:
            assertNoIndexes()
        when:
            locks.initialize()
        then:
            assertIndexesCreated()
    }

    def "should create sql indexes on first lock"() {
        expect:
            assertNoIndexes()
        when:
            locks.createLock("some-acquire")
                    .acquire()
        then:
            assertIndexesCreated()
    }

    boolean assertNoIndexes() {
        try (Connection connection = connectionPool.getConnection()) {
            assert listTableIndexes(connection, tableName).empty
        }
        return true
    }

    boolean assertIndexesCreated() {
        try (Connection connection = connectionPool.getConnection()) {
            assert listTableIndexes(connection, tableName) == expectedIndexNames
        }
        return true
    }

}
