package com.coditory.sherlock;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;

final class SqlQueries {
  private final String tableName;

  SqlQueries(String tableName) {
    this.tableName = expectValidTableName(tableName);
  }

  private String expectValidTableName(String tableName) {
    expectNonEmpty(tableName);
    if (!tableName.matches("[a-zA-Z0-9_]+")) {
      throw new IllegalArgumentException(
        "Expected table name consisting of a-z, A-Z, 0-9, _. Got: " + tableName);
    }
    return tableName;
  }

  String createLocksTable() {
    return "CREATE TABLE " + tableName
      + "("
      + "  ID VARCHAR(100) NOT NULL,"
      + "  ACQUIRED_BY VARCHAR(100) NOT NULL,"
      + "  ACQUIRED_AT TIMESTAMP(3) NOT NULL,"
      + "  EXPIRES_AT TIMESTAMP(3),"
      + "  PRIMARY KEY (ID)"
      + ")";
  }

  String checkTableExits() {
    return "SELECT 1 FROM " + tableName + " WHERE 1=2";
  }

  String deleteAll() {
    return "DELETE FROM " + tableName + ";";
  }

  String deleteAcquiredByIdAndOwnerId() {
    return "DELETE FROM " + tableName
      + " WHERE ID = ? AND ACQUIRED_BY = ? AND EXPIRES_AT > ?;";
  }

  String deleteAcquiredById() {
    return "DELETE FROM " + tableName
      + " WHERE ID = ? AND EXPIRES_AT > ?;";
  }

  String updateLockById() {
    return "UPDATE " + tableName
      + " SET ACQUIRED_BY = ?, ACQUIRED_AT = ?, EXPIRES_AT = ?"
      + " WHERE ID = ?";
  }

  String updateAcquiredOrReleasedLock() {
    return "UPDATE " + tableName
      + " SET ACQUIRED_BY = ?, ACQUIRED_AT = ?, EXPIRES_AT = ?"
      + " WHERE ID = ? AND (ACQUIRED_BY = ? OR EXPIRES_AT <= ?)";
  }

  String updateReleasedLock() {
    return "UPDATE " + tableName
      + " SET ACQUIRED_BY = ?, ACQUIRED_AT = ?, EXPIRES_AT = ?"
      + " WHERE ID = ? AND EXPIRES_AT <= ?";
  }

  String insertLock() {
    return "INSERT INTO " + tableName
      + " (ID, ACQUIRED_BY, ACQUIRED_AT, EXPIRES_AT)"
      + " VALUES (?, ?, ?, ?)";
  }
}
