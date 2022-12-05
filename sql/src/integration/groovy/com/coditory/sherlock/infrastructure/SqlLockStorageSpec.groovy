package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.*
import com.coditory.sherlock.base.LockTypes
import spock.lang.Unroll

import java.sql.*
import java.time.Duration
import java.time.Instant

class PostgresLockStorageSpec extends SqlLockStorageSpec implements PostgresConnectionProvider {}

class MySqlLockStorageSpec extends SqlLockStorageSpec implements MySqlConnectionProvider {}

abstract class SqlLockStorageSpec extends LocksBaseSpec implements UsesSqlSherlock {
    @Unroll
    def "should preserve lock state for acquired lock - #type"() {
        given:
            DistributedLock lock = createLock(type)
        when:
            lock.acquire()
        then:
            getLockRow() == [
                    "id"         : sampleLockId,
                    "acquired_by": sampleOwnerId,
                    "acquired_at": timestamp(),
                    "expires_at" : timestamp(defaultLockDuration)
            ]
        where:
            type << LockTypes.allLockTypes()
    }

    @Unroll
    def "should preserve lock state for acquired lock with custom duration - #type"() {
        given:
            DistributedLock lock = createLock(type)
            Duration duration = Duration.ofDays(1)
        when:
            lock.acquire(duration)
        then:
            getLockRow() == [
                    "id"         : sampleLockId,
                    "acquired_by": sampleOwnerId,
                    "acquired_at": timestamp(),
                    "expires_at" : timestamp(duration)
            ]
        where:
            type << LockTypes.allLockTypes()
    }

    @Unroll
    def "should preserve lock state for acquired infinite lock - #type"() {
        given:
            DistributedLock lock = createLock(type)
        when:
            lock.acquireForever()
        then:
            getLockRow() == [
                    "id"         : sampleLockId,
                    "acquired_by": sampleOwnerId,
                    "acquired_at": timestamp(),
                    "expires_at" : null
            ]
        where:
            type << LockTypes.allLockTypes()
    }

    @Unroll
    def "should not retrieve state of manually released lock - #type"() {
        given:
            DistributedLock lock = createLock(type)
            lock.acquire()
        when:
            lock.release()
        then:
            getLockRow() == null
        where:
            type << LockTypes.allLockTypes()
    }

    private Map<String, Object> getLockRow(String lockId = sampleLockId) {
        Map<String, Object> result
        try (
                Connection connection = connectionPool.getConnection()
                Statement statement = connection.createStatement()
                ResultSet resultSet = statement.executeQuery("SELECT * FROM locks WHERE ID = '$lockId';")
        ) {
            result = resultSetToList(resultSet)[0]
        }
        return result
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<String, Object>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i).toLowerCase(), rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }

    private Timestamp timestamp(Duration duration = Duration.ZERO) {
        Instant instant = fixedClock.instant().plus(duration)
        return new Timestamp(instant.toEpochMilli())
    }
}
