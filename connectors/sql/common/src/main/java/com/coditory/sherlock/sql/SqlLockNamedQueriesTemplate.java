package com.coditory.sherlock.sql;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class SqlLockNamedQueriesTemplate {
    private final String tableName;
    private final BindingMapper parameterMapper;

    public SqlLockNamedQueriesTemplate(@NotNull String tableName, @NotNull BindingMapper bindingMapper) {
        expectNonEmpty(tableName, "tableName");
        expectNonNull(bindingMapper, "bindingMapper");
        this.tableName = expectValidTableName(tableName);
        this.parameterMapper = bindingMapper;
    }

    private String expectValidTableName(String tableName) {
        if (!tableName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException(
                "Expected table name consisting of a-z, A-Z, 0-9, _. Got: " + tableName);
        }
        return tableName;
    }

    public String createLocksTable() {
        return "CREATE TABLE " + tableName
            + "("
            + "  ID VARCHAR(100) NOT NULL,"
            + "  ACQUIRED_BY VARCHAR(100) NOT NULL,"
            + "  ACQUIRED_AT TIMESTAMP(3) NOT NULL,"
            + "  EXPIRES_AT TIMESTAMP(3),"
            + "  PRIMARY KEY (ID)"
            + ")";
    }

    public String createLocksIndex() {
        return "CREATE INDEX " + tableName + "_IDX ON " + tableName + " (ID, ACQUIRED_BY, EXPIRES_AT)";
    }

    public String checkTableExits() {
        return "SELECT 1 FROM " + tableName + " WHERE 1=2";
    }

    public String deleteAll() {
        return "DELETE FROM " + tableName;
    }

    public String deleteAcquiredByIdAndOwnerId() {
        return String.format(
            "DELETE FROM %s"
                + " WHERE ID = %s AND ACQUIRED_BY = %s AND (EXPIRES_AT IS NULL OR EXPIRES_AT > %s)",
            tableName,
            getMarker(0, ParameterNames.LOCK_ID),
            getMarker(1, ParameterNames.OWNER_ID),
            getMarker(2, ParameterNames.NOW)
        );
    }

    public String deleteAcquiredById() {
        return String.format(
            "DELETE FROM %s"
                + " WHERE ID = %s AND (EXPIRES_AT IS NULL OR EXPIRES_AT > %s)",
            tableName,
            getMarker(0, ParameterNames.LOCK_ID),
            getMarker(1, ParameterNames.NOW)
        );
    }

    public String updateLockById() {
        return String.format("UPDATE %s"
                + " SET ACQUIRED_BY = %s, ACQUIRED_AT = %s, EXPIRES_AT = %s"
                + " WHERE ID = %s",
            tableName,
            getMarker(0, ParameterNames.OWNER_ID),
            getMarker(1, ParameterNames.NOW),
            getMarker(2, ParameterNames.EXPIRES_AT),
            getMarker(3, ParameterNames.LOCK_ID)
        );
    }

    public String updateAcquiredOrReleasedLock() {
        return String.format("UPDATE %s"
                + " SET ACQUIRED_BY = %s, ACQUIRED_AT = %s, EXPIRES_AT = %s"
                + " WHERE ID = %s AND (ACQUIRED_BY = %s OR EXPIRES_AT <= %s)",
            tableName,
            getMarker(0, ParameterNames.OWNER_ID),
            getMarker(1, ParameterNames.NOW),
            getMarker(2, ParameterNames.EXPIRES_AT),
            getMarker(3, ParameterNames.LOCK_ID),
            getMarker(4, ParameterNames.OWNER_ID),
            getMarker(5, ParameterNames.NOW)
        );
    }

    public String updateReleasedLock() {
        return String.format("UPDATE %s"
                + " SET ACQUIRED_BY = %s, ACQUIRED_AT = %s, EXPIRES_AT = %s"
                + " WHERE ID = %s AND EXPIRES_AT <= %s",
            tableName,
            getMarker(0, ParameterNames.OWNER_ID),
            getMarker(1, ParameterNames.NOW),
            getMarker(2, ParameterNames.EXPIRES_AT),
            getMarker(3, ParameterNames.LOCK_ID),
            getMarker(4, ParameterNames.NOW)
        );
    }

    public String insertLock() {
        return String.format("INSERT INTO %s"
                + " (ID, ACQUIRED_BY, ACQUIRED_AT, EXPIRES_AT)"
                + " VALUES (%s, %s, %s, %s)",
            tableName,
            getMarker(0, ParameterNames.LOCK_ID),
            getMarker(1, ParameterNames.OWNER_ID),
            getMarker(2, ParameterNames.NOW),
            getMarker(3, ParameterNames.EXPIRES_AT)
        );
    }

    private String getMarker(int index, String name) {
        return parameterMapper.mapBinding(index, name).getQueryMarker();
    }

    public static final class ParameterNames {
        public static final String LOCK_ID = "lockId";
        public static final String OWNER_ID = "ownerId";
        public static final String EXPIRES_AT = "expiresAt";
        public static final String NOW = "now";
        public static final List<String> ALL_PARAMS = List.of(
            LOCK_ID, OWNER_ID, EXPIRES_AT, NOW
        );
    }
}
