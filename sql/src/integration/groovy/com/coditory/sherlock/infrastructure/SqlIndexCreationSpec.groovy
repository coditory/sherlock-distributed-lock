package com.coditory.sherlock.infrastructure

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.MySqlConnectionProvider
import com.coditory.sherlock.base.PostgresConnectionProvider
import org.junit.After
import spock.lang.Specification

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
    .withConnection(connection)
    .withLocksTable(tableName)
    .build()

  abstract List<String> getExpectedIndexNames()

  abstract Connection getConnection();

  @After
  void dropTable() {
    Statement statement
    try {
      statement = connection.createStatement()
      statement.executeUpdate("DROP TABLE " + tableName)
    } finally {
      if (statement != null) statement.close()
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
    assert listTableIndexes(connection, tableName).empty
    return true
  }

  boolean assertIndexesCreated() {
    assert listTableIndexes(connection, tableName) == expectedIndexNames
    return true
  }

}
