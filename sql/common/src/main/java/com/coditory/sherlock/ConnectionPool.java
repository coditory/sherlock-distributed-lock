package com.coditory.sherlock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static java.util.Objects.requireNonNull;

@FunctionalInterface
interface ConnectionPool {
    static ConnectionPool of(DataSource dataSource) {
        return new DataSourceConnectionPool(dataSource);
    }

    static ConnectionPool of(Connection connection) {
        return new SingleConnectionPool(connection);
    }

    SqlConnection getConnection();
}

class DataSourceConnectionPool implements ConnectionPool {
    private final DataSource dataSource;

    public DataSourceConnectionPool(DataSource dataSource) {
        this.dataSource = requireNonNull(dataSource);
    }

    @Override
    public SqlConnection getConnection() {
        try {
            return new DataSourceConnection(dataSource.getConnection());
        } catch (SQLException e) {
            throw new SherlockException("Could not obtain connection from a dataSource", e);
        }
    }

    static class DataSourceConnection implements SqlConnection {
        private final Connection connection;

        public DataSourceConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return connection.prepareStatement(sql);
        }

        @Override
        public Statement createStatement() throws SQLException {
            return connection.createStatement();
        }

        @Override
        public void close() throws SQLException {
            connection.close();
        }
    }
}

class SingleConnectionPool implements ConnectionPool {
    private final Connection connection;

    public SingleConnectionPool(Connection connection) {
        this.connection = requireNonNull(connection);
    }

    @Override
    public SqlConnection getConnection() {
        return new UncloseableConnection(connection);
    }

    static class UncloseableConnection implements SqlConnection {
        private final Connection connection;

        public UncloseableConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return connection.prepareStatement(sql);
        }

        @Override
        public Statement createStatement() throws SQLException {
            return connection.createStatement();
        }

        @Override
        public void close() {
            // deliberately empty
        }
    }
}
