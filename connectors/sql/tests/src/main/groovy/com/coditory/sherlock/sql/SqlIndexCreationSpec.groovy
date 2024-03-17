package com.coditory.sherlock.sql

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import com.coditory.sherlock.base.UpdatableFixedClock
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.Connection
import java.sql.Statement
import java.time.Duration

import static SqlTableIndexes.listTableIndexes
import static com.coditory.sherlock.base.UpdatableFixedClock.defaultUpdatableFixedClock

abstract class SqlIndexCreationSpec extends Specification implements DistributedLocksCreator {
    static final String tableName = "other_locks"
    static final UpdatableFixedClock fixedClock = defaultUpdatableFixedClock()
    static final Duration defaultLockDuration = Duration.ofMinutes(10)
    static final String ownerId = "locks_test_instance"
    Sherlock locks

    abstract List<String> getExpectedIndexNames()

    abstract DataSource getDataSource()

    def setup() {
        locks = createSherlock(ownerId, defaultLockDuration, fixedClock, tableName)
    }

    void cleanup() {
        try (
                Connection connection = dataSource.getConnection()
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
        try (Connection connection = dataSource.getConnection()) {
            assert listTableIndexes(connection, tableName).empty
        }
        return true
    }

    boolean assertIndexesCreated() {
        try (Connection connection = dataSource.getConnection()) {
            assert listTableIndexes(connection, tableName) == expectedIndexNames
        }
        return true
    }
}
