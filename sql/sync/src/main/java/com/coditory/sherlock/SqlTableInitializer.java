package com.coditory.sherlock;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class SqlTableInitializer {
    private final SqlLockQueries sqlQueries;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    SqlTableInitializer(SqlLockQueries sqlQueries) {
        expectNonNull(sqlQueries, "sqlQueries");
        this.sqlQueries = sqlQueries;
    }

    PreparedStatement getInitializedStatement(SqlConnection connection, String sql) {
        if (initialized.compareAndSet(false, true)) {
            initialize(connection);
        }
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not create SQL statement", e);
        }
    }

    void initialize(SqlConnection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQueries.createLocksTable());
            statement.executeUpdate(sqlQueries.createLocksIndex());
            initialized.set(true);
        } catch (SQLException e) {
            try (PreparedStatement statement = connection
                    .prepareStatement(sqlQueries.checkTableExits())) {
                statement.executeQuery();
                // if no error then table exists
                initialized.set(true);
            } catch (SQLException checkException) {
                throw new IllegalStateException("Could not initialize locks table", e);
            }
        }
    }
}
