package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.BindingMapper
import com.coditory.sherlock.MySqlConnectionProvider
import com.coditory.sherlock.PostgresConnectionProvider
import com.coditory.sherlock.Sherlock
import io.r2dbc.spi.ConnectionFactory
import spock.lang.Specification

import java.sql.Connection
import java.sql.Statement

import static com.coditory.sherlock.BlockingKtSherlockWrapper.blockingKtSherlock
import static com.coditory.sherlock.KtSqlSherlockBuilder.coroutineSqlSherlock
import static com.coditory.sherlock.infrastructure.SqlTableIndexes.listTableIndexes

class KtPostgresIndexCreationSpec extends KtSqlIndexCreationSpec implements PostgresConnectionProvider {
    List<String> expectedIndexNames = [tableName + "_pkey", tableName + "_idx"].sort()
}

class KtMySqlIndexCreationSpec extends KtSqlIndexCreationSpec implements MySqlConnectionProvider {
    List<String> expectedIndexNames = ["PRIMARY", tableName + "_IDX"].sort()
}

abstract class KtSqlIndexCreationSpec extends Specification {
    String tableName = "other_locks"
    Sherlock locks = blockingKtSherlock(coroutineSqlSherlock()
            .withConnectionFactory(connectionFactory)
            .withLocksTable(tableName)
            .withBindingMapper(bindingMapper)
            .build())

    abstract List<String> getExpectedIndexNames()

    abstract ConnectionFactory getConnectionFactory();

    abstract Connection getBlockingConnection();

    abstract BindingMapper getBindingMapper();

    void cleanup() {
        try (
                Connection connection = getBlockingConnection()
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
        try (Connection connection = getBlockingConnection()) {
            assert listTableIndexes(connection, tableName).empty
        }
        return true
    }

    boolean assertIndexesCreated() {
        try (Connection connection = getBlockingConnection()) {
            assert listTableIndexes(connection, tableName) == expectedIndexNames
        }
        return true
    }

}
