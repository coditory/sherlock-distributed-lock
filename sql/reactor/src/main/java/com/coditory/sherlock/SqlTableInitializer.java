package com.coditory.sherlock;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

class SqlTableInitializer {
    private final SqlQueries sqlQueries;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    SqlTableInitializer(SqlQueries sqlQueries) {
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
            // TODO: Setup indexes
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
