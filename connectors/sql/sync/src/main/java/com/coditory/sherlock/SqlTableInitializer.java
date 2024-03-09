package com.coditory.sherlock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.coditory.sherlock.Preconditions.expectNonNull;

class SqlTableInitializer {
    private final SqlLockQueries sqlQueries;
    private final DataSource dataSource;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    SqlTableInitializer(SqlLockQueries sqlQueries, DataSource dataSource) {
        expectNonNull(sqlQueries, "sqlQueries");
        expectNonNull(dataSource, "dataSource");
        this.sqlQueries = sqlQueries;
        this.dataSource = dataSource;
    }

    Connection getInitializedConnection() throws SQLException {
        return initialized.compareAndSet(false, true)
                ? initialize()
                : getConnection();
    }

    private Connection initialize() throws SQLException {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQueries.createLocksTable());
            statement.executeUpdate(sqlQueries.createLocksIndex());
            connection.commit();
            initialized.set(true);
        } catch (SQLException e) {
            try (PreparedStatement statement = connection
                    .prepareStatement(sqlQueries.checkTableExits())) {
                statement.executeQuery();
                // no error means that the table exists
                initialized.set(true);
            } catch (SQLException checkException) {
                throw new IllegalStateException("Could not initialize locks table", e);
            }
        }
        return connection;
    }

    private Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);
        return connection;
    }
}
